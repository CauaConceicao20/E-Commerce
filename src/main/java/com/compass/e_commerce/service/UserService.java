package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.user.UserRegistrationDto;
import com.compass.e_commerce.model.role.Role;
import com.compass.e_commerce.model.role.RoleName;
import com.compass.e_commerce.model.user.User;
import com.compass.e_commerce.repository.RoleRepository;
import com.compass.e_commerce.repository.UserRepository;
//import com.compass.e_commerce.repository.UserRoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        Role role = roleRepository.findByName(RoleName.USER)
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
}
