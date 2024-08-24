package com.compass.e_commerce.dto.game;

import com.compass.e_commerce.model.game.GenderEnum;
import com.compass.e_commerce.model.game.PlatformEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record GameUpdateDto(
        @NotNull
        Long id,
        String name,
        String description,
        GenderEnum gender,
        int quantity,
        PlatformEnum platform,
        double price) {
}
