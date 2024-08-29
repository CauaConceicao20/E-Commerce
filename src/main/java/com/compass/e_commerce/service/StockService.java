package com.compass.e_commerce.service;

import com.compass.e_commerce.model.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    public void stockReduction(Game game, int quantity) {
        game.getStock().stockReduction(quantity);
    }

}
