package com.compass.e_commerce.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDto(

        @NotNull
        Long id,
        @NotBlank
        String login,
        @Email
        @NotBlank
        String email) {
}
