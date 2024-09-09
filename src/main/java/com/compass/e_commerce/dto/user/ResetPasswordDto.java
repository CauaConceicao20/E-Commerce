package com.compass.e_commerce.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDto(
        @Size(min = 8, max = 14)
        String newPassword
) {
}
