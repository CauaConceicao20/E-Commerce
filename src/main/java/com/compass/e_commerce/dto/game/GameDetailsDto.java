package com.compass.e_commerce.dto.game;

import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.enums.GenderEnum;
import com.compass.e_commerce.model.enums.PlatformEnum;

public record GameDetailsDto(Long id, String name, String description, GenderEnum gender, int stock, PlatformEnum platform, double price) {

    public GameDetailsDto(Game game) {
        this(game.getId(),
           game.getName(),
           game.getDescription(),
           game.getGender(),
           game.getStock().getQuantity(),
           game.getPlatform(),
           game.getPrice());

    }

}
