package com.compass.e_commerce.dto.game;

import com.compass.e_commerce.model.game.GenderEnum;
import com.compass.e_commerce.model.game.PlatformEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record GameRegistrationDto(
        @NotEmpty
        String name,
        @NotEmpty
        String description,
        @NotNull
        GenderEnum gender,
        @Min(1)
        int quantity,
        @NotNull
        PlatformEnum platform,
        @DecimalMin("1.0")
        double price
) {
}
