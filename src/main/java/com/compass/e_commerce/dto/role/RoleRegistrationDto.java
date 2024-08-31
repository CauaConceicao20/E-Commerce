package com.compass.e_commerce.dto.role;

import com.compass.e_commerce.annotations.UniqueNameRole;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import jakarta.validation.constraints.NotNull;

public record RoleRegistrationDto(
        @NotNull
        @UniqueNameRole
        RoleNameEnum name) {
}
