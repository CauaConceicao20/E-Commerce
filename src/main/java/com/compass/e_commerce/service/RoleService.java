package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.role.RoleRegistrationDto;
import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Role create(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> list() {
        return roleRepository.findAll();
    }

    public Role convertDtoToEntity(RoleRegistrationDto roleRegistrationDto) {
        return new Role(roleRegistrationDto);
    }

}
