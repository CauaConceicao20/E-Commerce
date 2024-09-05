package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.annotations.UniqueLoginUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserAuthenticationDto(
        @NotBlank
        String login,
        @NotBlank
        @Size(min = 8, max = 14)
        String password) {
}
