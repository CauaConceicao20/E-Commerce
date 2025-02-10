package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.role.RoleRegistrationDto;
import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.repository.RoleRepository;
import com.compass.e_commerce.service.interfaces.CrudService;
import com.compass.e_commerce.service.interfaces.RoleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements CrudService<Role> ,RoleService<Role, RoleRegistrationDto> {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    @CacheEvict(value = "roles", allEntries = true)
    public Role create(Role role) {
        return roleRepository.save(role);
    }

    @Override
    @Cacheable("roles")
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role convertDtoToEntity(RoleRegistrationDto roleRegistrationDto) {
        return new com.compass.e_commerce.model.Role(roleRegistrationDto);
    }

    @Override
    @Cacheable("roles")
    public Role getById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role não encontrada com id: " + id));
    }
}
