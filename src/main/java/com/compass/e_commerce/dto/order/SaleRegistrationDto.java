package com.compass.e_commerce.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record SaleRegistrationDto(
        @Valid
        @NotEmpty
        List<SaleGameRegistrationDto> games
) {
    public static record SaleGameRegistrationDto(
            @Positive
            @NotNull
            Long gameId,
            @Positive
            Integer quantity
    ) {
    }
}
