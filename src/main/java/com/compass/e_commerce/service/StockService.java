package com.compass.e_commerce.service;

import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.OrderGames;
import com.compass.e_commerce.service.interfaces.StockServiceImp;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StockService implements StockServiceImp {

    @Transactional
    public void stockReduction(Game game, int quantity) {
        game.getStock().stockReduction(quantity);
    }

    @Transactional
    public void stockReplenishment(Game game, int quantity) {
        game.getStock().stockReplenishment(quantity);
    }

    @Transactional
    public void adjustStockBasedOnSaleQuantityChange(OrderGames existingOrderGames, int newQuantity) {
        int currentQuantity = existingOrderGames.getQuantity();
        int quantityDifference = newQuantity - currentQuantity;

        if (quantityDifference > 0) {
            stockReduction(existingOrderGames.getGame(), quantityDifference);
        } else if (quantityDifference < 0) {
            stockReplenishment(existingOrderGames.getGame(), -quantityDifference);
        }
    }
}
