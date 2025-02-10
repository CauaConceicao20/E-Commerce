package com.compass.e_commerce.service;

import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.OrderGames;
import com.compass.e_commerce.service.interfaces.StockService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl implements StockService<Game, OrderGames> {

    @Override
    @Transactional
    public void stockReduction(Game game, int quantity) {
        game.getStock().stockReduction(quantity);
    }

    @Override
    @Transactional
    public void stockReplenishment(Game game, int quantity) {
        game.getStock().stockReplenishment(quantity);
    }

    @Override
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
