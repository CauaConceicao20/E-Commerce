package com.compass.e_commerce.dto.game;

import com.compass.e_commerce.model.enums.GenderEnum;
import com.compass.e_commerce.model.enums.PlatformEnum;
import com.compass.e_commerce.dto.stock.StockDto;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameRegistrationDto(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotNull
        @Enumerated
        GenderEnum gender,
        @NotNull
        @Valid
        StockDto stock,
        @NotNull
        @Enumerated
        PlatformEnum platform,
        @DecimalMin("1.0")
        double price
) {
}
