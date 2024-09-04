package com.compass.e_commerce.dto.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record SaleUpdateDto(
        @NotNull
        Long saleId,
        @NotEmpty
        @Valid
        List<SaleGameUpdateDto> games
) {
    public static record SaleGameUpdateDto(
            @NotNull
            Long gameId,
            @Positive
            Integer quantity
    ){

    }
}
