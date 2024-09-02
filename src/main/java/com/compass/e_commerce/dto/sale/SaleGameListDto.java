package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.model.SaleGame;

import java.io.Serializable;

public record SaleGameListDto(
        Long gameId,
        String gameName,
        Integer quantity,
        Double price
) implements Serializable {
    private static final long serialVersionUID = 1L;

    public SaleGameListDto(SaleGame saleGame) {
        this(saleGame.getGame().getId(),
                saleGame.getGame().getName(),
                saleGame.getQuantity(),
                saleGame.getGame().getPrice()
        );
    }
}
