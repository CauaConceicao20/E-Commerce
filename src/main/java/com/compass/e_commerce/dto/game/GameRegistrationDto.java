package com.compass.e_commerce.dto.game;

import com.compass.e_commerce.model.game.GenderEnum;
import com.compass.e_commerce.model.game.PlatformEnum;
import com.compass.e_commerce.model.stock.StockDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record GameRegistrationDto(
        @NotEmpty
        String name,
        @NotEmpty
        String description,
        @NotNull
        GenderEnum gender,
        @NotNull
        @Valid
        StockDto stock,
        @NotNull
        PlatformEnum platform,
        @DecimalMin("1.0")
        double price
) {
}
