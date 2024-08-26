package com.compass.e_commerce.dto.role;


import com.compass.e_commerce.model.role.RoleName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record RoleDto(
        @Enumerated(EnumType.STRING)
        @NotEmpty
        Set<RoleName> roles) {
}
