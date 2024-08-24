package com.compass.e_commerce.dto.game;

import com.compass.e_commerce.model.game.Game;
import com.compass.e_commerce.model.game.GenderEnum;
import com.compass.e_commerce.model.game.PlatformEnum;

public record GameListDto(
        Long id,
        String name,
        String description,
        GenderEnum gender,
        int quantity,
        PlatformEnum platform,
        Double price) {

    public GameListDto(Game game) {
       this(game.getId(),
           game.getName(),
           game.getDescription(),
           game.getGender(),
           game.getQuantity(),
           game.getPlatform(),
           game.getPrice());
    }
}
