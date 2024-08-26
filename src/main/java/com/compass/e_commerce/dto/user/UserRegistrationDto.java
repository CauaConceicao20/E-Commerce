package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.annotations.UniqueLoginUser;
import com.compass.e_commerce.dto.role.RoleDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


public record UserRegistrationDto(
        @UniqueLoginUser
        String login,
        String password,
        @Valid
        @NotNull
        RoleDto rolesDto


) {
}
