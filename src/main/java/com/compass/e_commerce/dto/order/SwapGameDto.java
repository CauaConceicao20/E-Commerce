package com.compass.e_commerce.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record SwapGameDto(
        @NotNull
        Long id,
        @Valid
        @NotEmpty
        List<SwapGameListDto> swapGames
) {
    public static record SwapGameListDto(
            @NotNull
            Long currentGameId,
            @NotNull
            Long newGameId,
            @Positive
            Integer quantity
    ) {

    }
}
