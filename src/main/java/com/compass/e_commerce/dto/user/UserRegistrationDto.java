package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.dto.role.RoleDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


public record UserRegistrationDto(
        String login,
        String password,
        @Valid
        @NotNull
        RoleDto rolesDto


) {
}
