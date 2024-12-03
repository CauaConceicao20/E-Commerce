package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.role.RoleRegistrationDto;
import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.repository.RoleRepository;
import com.compass.e_commerce.service.interfaces.RoleServiceImp;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleServiceImp {

    private final RoleRepository roleRepository;

    @Transactional
    @CacheEvict(value = "roles", allEntries = true)
    public Role create(Role role) {
        return roleRepository.save(role);
    }

    @Cacheable("roles")
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public Role convertDtoToEntity(RoleRegistrationDto roleRegistrationDto) {
        return new Role(roleRegistrationDto);
    }

}
