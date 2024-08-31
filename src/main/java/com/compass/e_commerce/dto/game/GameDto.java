package com.compass.e_commerce.dto.game;

public record GameDto(
        Long gameId,
        String gameName,
        Integer quantity,
        Double price
) {
}
