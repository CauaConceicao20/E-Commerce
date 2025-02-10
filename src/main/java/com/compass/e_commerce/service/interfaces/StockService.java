package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.OrderGames;

public interface StockService<T, OG> {

    void stockReduction(T entity, int quantity);

    void stockReplenishment(T entity, int quantity);

    void adjustStockBasedOnSaleQuantityChange(OG og, int quantity);
}
