package com.compass.e_commerce.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderUpdateDto(
        @NotEmpty
        @Valid
        List<OrderGameUpdateDto> games
) {
    public static record OrderGameUpdateDto(
            @NotNull
            Long gameId,
            @Positive
            Integer quantity
    ){

    }
}
