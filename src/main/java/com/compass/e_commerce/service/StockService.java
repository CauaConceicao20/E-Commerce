package com.compass.e_commerce.service;

import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.SaleGame;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    @Transactional
    public void stockReduction(Game game, int quantity) {
        game.getStock().stockReduction(quantity);
    }

    @Transactional
    public void stockReplenishment(Game game, Integer quantity) {
        game.getStock().stockReplenishment(quantity);
    }

    @Transactional
    public void adjustStockBasedOnSaleQuantityChange(SaleGame existingSaleGame, int newQuantity) {
        int currentQuantity = existingSaleGame.getQuantity();
        int quantityDifference = newQuantity - currentQuantity;

        if (quantityDifference > 0) {
            stockReduction(existingSaleGame.getGame(), quantityDifference);
        } else if (quantityDifference < 0) {
            stockReplenishment(existingSaleGame.getGame(), -quantityDifference);
        }
    }
}
