package com.compass.e_commerce.dto.order;

import com.compass.e_commerce.model.OrderGames;

import java.io.Serializable;

public record OrderGameListDto(

        Long gameId,
        String gameName,
        Integer quantity,
        Double price
        
) implements Serializable {
    private static final long serialVersionUID = 1L;

    public OrderGameListDto(OrderGames orderGames) {
        this(orderGames.getGame().getId(),
                orderGames.getGame().getName(),
                orderGames.getQuantity(),
                orderGames.getGame().getPrice()
        );
    }
}
