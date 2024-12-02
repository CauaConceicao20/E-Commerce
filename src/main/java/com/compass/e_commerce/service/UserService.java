package com.compass.e_commerce.service;

import com.compass.e_commerce.config.security.UserDetailsImpl;
import com.compass.e_commerce.dto.user.UserRegistrationDto;
import com.compass.e_commerce.dto.user.UserUpdateDto;
import com.compass.e_commerce.exception.personalized.AccessRestrictException;
import com.compass.e_commerce.exception.personalized.DeletionNotAllowedException;
import com.compass.e_commerce.exception.personalized.UserInactiveException;
import com.compass.e_commerce.model.Cart;
import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.repository.RoleRepository;
import com.compass.e_commerce.repository.UserRepository;
import com.compass.e_commerce.service.interfaces.UserServiceInterface;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

    String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Transactional
    public User convertDtoToEntity(UserRegistrationDto userRegistrationDto) {
        Cart cart = new Cart();
        cartService.create(cart);
        return new User(userRegistrationDto, cart);
    }

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

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User registerUserAdmin(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        Set<Role> roles = new HashSet<>(roleRepository.findAll());

        if(roles.isEmpty()) {
            throw new EntityNotFoundException("Role não encontrada");
        }else {
            user.setRoles(roles);
            return userRepository.save(user);
        }
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User não encontrado id: " + id));
    }

    public Long getAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            var userDetails = (UserDetailsImpl) authentication.getPrincipal();
            if (userDetails != null) {
                return userDetails.getId();
            }
        }
        throw new UsernameNotFoundException("User não encontrado");
    }

    @Cacheable("users")
    public List<User> getAll() {
        return userRepository.findByActiveTrue();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User não encontrado login: " + email));
    }

    public User findByLogin(String login) {
      return userRepository.findByLogin(login)
               .orElseThrow(() -> new UsernameNotFoundException("Usuario ou senha invalidos ou incorretos"));
    }

    @Transactional
    public User changePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User updateByAdmin(Long id, UserUpdateDto userUpdateDto) {
        User foundUser = getById(id);
        logicUpdate(foundUser, userUpdateDto);
        userRepository.save(foundUser);

        return foundUser;
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User updateMyProfile(UserUpdateDto userUpdateDto) {
        User user = getById(getAuthenticatedUserId());
        logicUpdate(user, userUpdateDto);
        userRepository.save(user);
        return user;
    }

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
        if(userUpdateDto.password() != null) {
            if (userUpdateDto.password().length() < 8 || userUpdateDto.password().length() > 14) {
                throw new IllegalArgumentException("A senha deve ter entre 8 e 14 caracteres.");
            } else {
                String encryptedPassword = passwordEncoder.encode(userUpdateDto.password());
                user.setPassword(encryptedPassword);
            }
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User não encontrado com o id: " + id));

        if(!user.getActive()) {
            throw new UserInactiveException("Usuário está inativo");
        }

        if(!user.getSales().isEmpty()) {
            throw new DeletionNotAllowedException("O Usuario está associado a vendas.");
        }
        userRepository.deleteById(id);
    }
}