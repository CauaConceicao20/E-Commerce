package com.compass.e_commerce.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderRegistrationDto(
        @Valid
        @NotEmpty
        List<OrderGameRegistrationDto> games
) {
    public static record OrderGameRegistrationDto(
            @Positive
            @NotNull
            Long gameId,
            @Positive
            Integer quantity
    ) {
    }
}
