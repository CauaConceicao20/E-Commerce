package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.annotations.UniqueEmail;
import com.compass.e_commerce.annotations.UniqueLoginUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


public record UserRegistrationDto(
        @UniqueLoginUser
        String login,

        @Size(min = 8, max = 14)
        String password,

        @Email
        @UniqueEmail
        @NotBlank
        String email

) {
}
