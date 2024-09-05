package com.compass.e_commerce.service;

import com.compass.e_commerce.config.security.UserDetailsImpl;
import com.compass.e_commerce.dto.user.AdminUpdateDto;
import com.compass.e_commerce.dto.user.UserRegistrationDto;
import com.compass.e_commerce.dto.user.UserUpdateDto;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.repository.RoleRepository;
import com.compass.e_commerce.repository.UserRepository;
//import com.compass.e_commerce.repository.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User convertDtoToEntity(UserRegistrationDto userRegistrationDto) {
        return new User(userRegistrationDto);
    }

    public User registerUser(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        Role role = roleRepository.findByName(RoleNameEnum.USER)
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada"));

        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }

    public User registerUserAdmin(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        Set<Role> roles = new HashSet<Role>(roleRepository.findAll());

        if(roles.isEmpty()) {
            throw new EntityNotFoundException("Role não encontrada");
        }else {
            user.setRoles(roles);
            return userRepository.save(user);
        }
    }

    public User findById(Long id) {
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

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByLogin(email)
                .orElseThrow(() -> new EntityNotFoundException("User não encontrado login: " + email));
    }

    public User findByLogin(String login) {
       return userRepository.findByLogin(login)
               .orElseThrow(() -> new EntityNotFoundException("User não encontrado login: " + login));
    }

    @Transactional
    public User changePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(UserUpdateDto userUpdateDto) {
        User user = userRepository.findByIdUser(userUpdateDto.id(), RoleNameEnum.USER)
                .orElseThrow(() -> new EntityNotFoundException("User não encontrado"));

        if (userUpdateDto.login() != null) {
            user.setLogin(userUpdateDto.login());
        }
        if (userUpdateDto.email() != null) {
            if (!EMAIL_PATTERN.matcher(userUpdateDto.email()).matches()) {
                throw new IllegalArgumentException("O e-mail fornecido é inválido.");
            }
            user.setEmail(userUpdateDto.email());
        }
        userRepository.save(user);

        return user;
    }

    @Transactional
    public User updateAdmin(AdminUpdateDto adminUpdateDto) {
        User user = userRepository.findByIdAdmin(adminUpdateDto.id(), RoleNameEnum.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("User não encontrado"));

        if (adminUpdateDto.login() != null) {
            user.setLogin(adminUpdateDto.login());
        }
        if (adminUpdateDto.email() != null) {
            if (!EMAIL_PATTERN.matcher(adminUpdateDto.email()).matches()) {
                throw new IllegalArgumentException("O e-mail fornecido é inválido.");
            }
            user.setEmail(adminUpdateDto.email());
        }
        if(adminUpdateDto.password() != null) {
            if (adminUpdateDto.password().length() < 8 || adminUpdateDto.password().length() > 14) {
                throw new IllegalArgumentException("A senha deve ter entre 8 e 14 caracteres.");
            } else {
                String encryptedPassword = passwordEncoder.encode(adminUpdateDto.password());
                user.setPassword(encryptedPassword);
            }
        }
        userRepository.save(user);

        return user;
    }


    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
