package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.user.AddressDataDto;
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
public class UserServiceImpl implements CrudService<User>, OptionalCrudMethods<User, UserUpdateDto>,
        UserService<User, UserUpdateDto> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationServiceImpl authenticationServiceImpl;

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
    public User registerUser(User user) {

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        Role role = roleRepository.findByName(RoleNameEnum.USER)
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada"));

        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User registerUserAdmin(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        Set<Role> roles = new HashSet<>(roleRepository.findAll());

        if (roles.isEmpty()) {
            throw new EntityNotFoundException("Roles não foram encontradas");
        } else {
            user.setRoles(roles);
            return userRepository.save(user);
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
                .orElseThrow(() -> new EntityNotFoundException("User não encontrado username: " + email));
    }

    @Override
    @Cacheable("users")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
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
    public User changePassword(User user, String newPassword) {
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User update(Long id, UserUpdateDto userUpdateDto) {
        User authenticatedUser = getById(authenticationServiceImpl.getAuthenticatedUserId());
        boolean isAdmin = authenticatedUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleNameEnum.ADMIN));

        if (id == null) {
            if (authenticatedUser.getActive() && isAdmin) {
                logicUpdate(authenticatedUser, userUpdateDto);
                return userRepository.save(authenticatedUser);
            } else {
                throw new IllegalArgumentException("Usuário não tem permissão para atualizar ou está inativo.");
            }
        } else {
            User user = getById(id);
            if (user != null && (user.getActive() && isAdmin)) {
                logicUpdate(user, userUpdateDto);
                return userRepository.save(user);
            } else {
                throw new IllegalArgumentException("ID não corresponde a um usuário válido ou permissão insuficiente.");
            }
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User updateMyProfile(UserUpdateDto userUpdateDto) {
        User user = getById(authenticationServiceImpl.getAuthenticatedUserId());
        logicUpdate(user, userUpdateDto);
        return userRepository.save(user);
    }

    private void logicUpdate(User user, UserUpdateDto userUpdateDto) {
        updatePersonalDataLogic(user, userUpdateDto);
        updateAddressLogic(user, userUpdateDto);
    }

    private void updatePersonalDataLogic(User user, UserUpdateDto userUpdateDto) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@(hotmail\\.com|gmail\\.com)$";
        Pattern pattern = null;

        if (!user.getActive()) {
            throw new UserInactiveException("Usuário está inativo");
        }

        if (userUpdateDto.login() != null) {
            if(userUpdateDto.login().isBlank()) {
                throw new IllegalArgumentException("Username não deve está vazia");
            }
            user.setUsername(userUpdateDto.login());
        }
        if (userUpdateDto.email() != null) {
            pattern = Pattern.compile(emailRegex);
            if (!pattern.matcher(userUpdateDto.email()).matches()) {
                throw new IllegalArgumentException("O e-mail fornecido é inválido.");
            }
            user.setEmail(userUpdateDto.email());
        }
        if (userUpdateDto.password() != null) {
            if (userUpdateDto.password().length() < 8 || userUpdateDto.password().length() > 14) {
                throw new IllegalArgumentException(" Password deve ter entre 8 e 14 caracteres.");
            }
                String encryptedPassword = passwordEncoder.encode(userUpdateDto.password());
                user.setPassword(encryptedPassword);
        }
        if(userUpdateDto.phone() != null) {
            if(!(userUpdateDto.phone().length() == 11 && userUpdateDto.phone().matches("\\d+"))) {
                throw new IllegalArgumentException("O Phone fornecido é inválido");
            }
            user.setPhone(userUpdateDto.phone());
        }
    }

    private void updateAddressLogic(User user, UserUpdateDto userUpdateDto){
        String regexCep = "^(\\d{5}-\\d{3}|\\d{8})$";
        Pattern cepPattern = Pattern.compile(regexCep);

        if (!user.getActive()) {
            throw new UserInactiveException("Usuário está inativo");
        }

        if(userUpdateDto.addressDataDto().street() != null) {
            if(userUpdateDto.addressDataDto().street().isBlank()) {
                throw new IllegalArgumentException("street não deve está vazia");
            }
            user.getAddress().setStreet(userUpdateDto.addressDataDto().street());
        }
        if(userUpdateDto.addressDataDto().number() != null) {
            if(userUpdateDto.addressDataDto().number() <= 0) {
                throw new IllegalArgumentException("O numero não pode ser zero ou menos");
            }
            user.getAddress().setNumber(userUpdateDto.addressDataDto().number());
        }
        if(userUpdateDto.addressDataDto().city() != null) {
            if(userUpdateDto.addressDataDto().city().isBlank()) {
                throw new IllegalArgumentException("city não deve está vazia");
            }
            user.getAddress().setCity(userUpdateDto.addressDataDto().city());
        }
        if(userUpdateDto.addressDataDto().state() != null) {
            if(userUpdateDto.addressDataDto().state().trim().isBlank()) {
                throw new IllegalArgumentException("state não deve está vazia");
            }
            user.getAddress().setState(userUpdateDto.addressDataDto().state());
        }
        if(userUpdateDto.addressDataDto().postalCode() != null) {
            if(!cepPattern.matcher(userUpdateDto.addressDataDto().postalCode()).matches()) {
                throw new IllegalArgumentException("Cep está incorreto");
            }
            user.getAddress().setPostalCode(userUpdateDto.addressDataDto().postalCode());
        }
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void delete(Long id) {
        User user = getById(id);

        if (!user.getActive()) {
            throw new UserInactiveException("Usuário está inativo");
        }

        if (!user.getOrders().isEmpty()) {
            throw new DeletionNotAllowedException("O Usuario está associado a pedidos.");
        }
        userRepository.deleteById(id);
    }
}