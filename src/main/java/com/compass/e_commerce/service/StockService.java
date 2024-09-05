package com.compass.e_commerce.service;

import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.SaleGame;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    public void stockReduction(Game game, int quantity) {
        game.getStock().stockReduction(quantity);
    }

    public void stockReposition(Game game, Integer quantity) {
        game.getStock().stockReplenishment(quantity);
    }

    public void adjustStockBasedOnSaleQuantityChange(SaleGame existingSaleGame, int novaQuantidade) {
        int quantidadeAtual = existingSaleGame.getQuantity();
        int diferencaQuantidade = novaQuantidade - quantidadeAtual;

        if (diferencaQuantidade > 0) {
            stockReduction(existingSaleGame.getGame(), diferencaQuantidade);
        } else if (diferencaQuantidade < 0) {
            stockReposition(existingSaleGame.getGame(), -diferencaQuantidade);
        }
    }
}
