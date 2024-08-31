package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.role.RoleRegistrationDto;
import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role create(Role role) {
        return roleRepository.save(role);
    }

    public Role convertDtoToEntity(RoleRegistrationDto roleRegistrationDto) {
        return new Role(roleRegistrationDto);
    }
}
