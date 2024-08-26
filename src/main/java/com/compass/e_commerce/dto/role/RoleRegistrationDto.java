package com.compass.e_commerce.dto.role;

import com.compass.e_commerce.annotations.UniqueNameRole;
import com.compass.e_commerce.model.role.RoleName;
import jakarta.validation.constraints.NotNull;

public record RoleRegistrationDto(
        @NotNull
        @UniqueNameRole
        RoleName name) {
}
