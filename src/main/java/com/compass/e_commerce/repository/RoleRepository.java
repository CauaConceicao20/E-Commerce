package com.compass.e_commerce.repository;

import com.compass.e_commerce.model.role.Role;
import com.compass.e_commerce.model.role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName role);

    boolean existsByName(RoleName name);
}
