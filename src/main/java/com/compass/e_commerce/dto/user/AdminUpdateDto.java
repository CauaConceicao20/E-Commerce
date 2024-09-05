package com.compass.e_commerce.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AdminUpdateDto(

        @NotNull
        Long id,

        String login,
        @Email
        String email,

        String password
        ) {
}