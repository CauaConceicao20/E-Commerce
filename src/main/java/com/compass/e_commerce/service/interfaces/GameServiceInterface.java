package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.model.Game;

import java.util.List;

public interface GameServiceInterface {
    Game create(Game game);
    List<Game> getAll();
    Game getById(Long id);
    Game update(GameUpdateDto gameUpdateDto);
    void delete(Long id);
}
