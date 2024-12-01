package com.compass.e_commerce.dto.game;

import com.compass.e_commerce.model.enums.GenderEnum;
import com.compass.e_commerce.model.enums.PlatformEnum;
import jakarta.validation.constraints.NotNull;

public record GameUpdateDto(
        String name,
        String description,
        GenderEnum gender,
        PlatformEnum platform,
        double price) {
}
