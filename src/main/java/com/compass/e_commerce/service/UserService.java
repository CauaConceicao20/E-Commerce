package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.user.UserRegistrationDto;
import com.compass.e_commerce.model.role.Role;
import com.compass.e_commerce.model.role.RoleName;
import com.compass.e_commerce.model.user.User;
import com.compass.e_commerce.repository.RoleRepository;
import com.compass.e_commerce.repository.UserRepository;
//import com.compass.e_commerce.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User convertDtoToEntity(UserRegistrationDto userRegistrationDto) {
        return new User(userRegistrationDto);
    }

    public User create(User user, Set<RoleName> roles) {
        Set<Role> roleSet = roles.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roleSet);
        return userRepository.save(user);
    }
}
