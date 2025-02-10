package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.user.UserRegistrationDto;
import com.compass.e_commerce.dto.user.UserUpdateDto;
import com.compass.e_commerce.exception.personalized.DeletionNotAllowedException;
import com.compass.e_commerce.exception.personalized.UserInactiveException;
import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.repository.RoleRepository;
import com.compass.e_commerce.repository.UserRepository;
import com.compass.e_commerce.service.interfaces.CrudService;
import com.compass.e_commerce.service.interfaces.OptionalCrudMethods;
import com.compass.e_commerce.service.interfaces.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements CrudService<User>, OptionalCrudMethods<User, UserUpdateDto>, UserService<User, UserUpdateDto>
{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationServiceImpl authenticationServiceImpl;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Transactional
    public User convertDtoToEntity(UserRegistrationDto userRegistrationDto) {
        return new User(userRegistrationDto);
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void registerUser(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        Role role = roleRepository.findByName(RoleNameEnum.USER)
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada"));

        user.setRoles(Set.of(role));
        create(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void registerUserAdmin(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        Set<Role> roles = new HashSet<>(roleRepository.findAll());

        if (roles.isEmpty()) {
            throw new EntityNotFoundException("Role não encontrada");
        } else {
            user.setRoles(roles);
            userRepository.save(user);
        }
    }

    @Override
    @Cacheable("users")
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User não encontrado id: " + id));
    }

    @Override
    @Cacheable("users")
    public List<User> getAll() {
        return userRepository.findByActiveTrue();
    }

    @Override
    @Cacheable("users")
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User não encontrado login: " + email));
    }

    @Override
    @Cacheable("users")
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario ou senha invalidos ou incorretos"));
    }

    @Override
    @Cacheable("users")
    public User findByCpf(String cpf) {
        return userRepository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado cpf : " + cpf));
    }

    @Override
    @Transactional
    public void changePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User update(Long id, UserUpdateDto userUpdateDto) {
        User authenticadeUser = getById(authenticationServiceImpl.getAuthenticatedUserId());
        boolean isAdmin = authenticadeUser.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleNameEnum.ADMIN);

        if (id == null && authenticadeUser.getActive() && isAdmin) {
            logicUpdate(authenticadeUser, userUpdateDto);
            return authenticadeUser;
        } else if (id != null) {
            User user = getById(id);
            logicUpdate(user, userUpdateDto);
            return user;
        } else {
            throw new IllegalArgumentException("ID não fornecido e usuário não tem permissão para atualizar.");
        }
    }

    /*@Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User updateByAdmin(Long id, UserUpdateDto userUpdateDto) {
        User foundUser = getById(id);
        logicUpdate(foundUser, userUpdateDto);
        userRepository.save(foundUser);

        return foundUser;
    }
     */

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User updateMyProfile(UserUpdateDto userUpdateDto) {
        User user = getById(authenticationServiceImpl.getAuthenticatedUserId());
        logicUpdate(user, userUpdateDto);
        userRepository.save(user);
        return user;
    }

    @Override
    public void logicUpdate(User user, UserUpdateDto userUpdateDto) {
        if (!user.getActive()) {
            throw new UserInactiveException("Usuário está inativo");
        }
        if (userUpdateDto.login() != null) {
            user.setLogin(userUpdateDto.login());
        }
        if (userUpdateDto.email() != null) {
            if (!EMAIL_PATTERN.matcher(userUpdateDto.email()).matches()) {
                throw new IllegalArgumentException("O e-mail fornecido é inválido.");
            }
            user.setEmail(userUpdateDto.email());
        }
        if (userUpdateDto.password() != null) {
            if (userUpdateDto.password().length() < 8 || userUpdateDto.password().length() > 14) {
                throw new IllegalArgumentException("A senha deve ter entre 8 e 14 caracteres.");
            } else {
                String encryptedPassword = passwordEncoder.encode(userUpdateDto.password());
                user.setPassword(encryptedPassword);
            }
        }
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User não encontrado com o id: " + id));

        if (!user.getActive()) {
            throw new UserInactiveException("Usuário está inativo");
        }

        if (!user.getOrders().isEmpty()) {
            throw new DeletionNotAllowedException("O Usuario está associado a vendas.");
        }
        userRepository.deleteById(id);
    }
}