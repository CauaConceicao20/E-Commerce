package com.compass.e_commerce.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDto(
        @Email
        @NotBlank
        String email) {
}
