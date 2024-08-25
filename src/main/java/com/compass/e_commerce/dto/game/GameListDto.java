package com.compass.e_commerce.dto.game;

import com.compass.e_commerce.model.game.Game;
import com.compass.e_commerce.model.game.GenderEnum;
import com.compass.e_commerce.model.game.PlatformEnum;
import com.compass.e_commerce.model.stock.StockDto;

public record GameListDto(
        Long id,
        String name,
        String description,
        GenderEnum gender,
        int stock,
        PlatformEnum platform,
        Double price) {

    public GameListDto(Game game) {
       this(game.getId(),
           game.getName(),
           game.getDescription(),
           game.getGender(),
           game.getStock().getQuantity(),
           game.getPlatform(),
           game.getPrice());
    }
}
