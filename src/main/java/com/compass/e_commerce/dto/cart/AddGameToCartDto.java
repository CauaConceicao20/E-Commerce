package com.compass.e_commerce.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddGameToCartDto(
        @NotNull
        Long gameId,

        @Min(1)
        int quantityGameInCart
) {
}
