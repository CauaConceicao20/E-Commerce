package com.compass.e_commerce.service;

import com.compass.e_commerce.config.security.UserDetailsImpl;
import com.compass.e_commerce.dto.user.UserRegistrationDto;
import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.repository.RoleRepository;
import com.compass.e_commerce.repository.UserRepository;
//import com.compass.e_commerce.repository.UserRoleRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }

    public User registerUserAdmin(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        Set<Role> roles = new HashSet<Role>(roleRepository.findAll());

        if(roles.isEmpty()) {
            throw new RuntimeException("Role not found");
        }else {
            user.setRoles(roles);
            return userRepository.save(user);
        }
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Long getAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            var userDetails = (UserDetailsImpl) authentication.getPrincipal();
            if (userDetails != null) {
                return userDetails.getId();
            }
        }
        throw new UsernameNotFoundException("User Not Found");
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User changePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }
}
