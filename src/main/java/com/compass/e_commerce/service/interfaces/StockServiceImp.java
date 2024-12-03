package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.SaleGame;

public interface StockServiceImp {

    void stockReduction(Game game, int quantity);

    void stockReplenishment(Game game, int quantity);

    void adjustStockBasedOnSaleQuantityChange(SaleGame saleGame, int quantity);
}
