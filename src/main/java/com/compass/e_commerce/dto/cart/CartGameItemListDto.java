package com.compass.e_commerce.dto.cart;

import com.compass.e_commerce.model.CartGameItem;

public record CartGameItemListDto(
        Long id,
        String gameTitle,
        double gamePrice,
        int quantity) {

   public CartGameItemListDto(CartGameItem cartGameItem) {
        this(cartGameItem.getGame().getId(),
             cartGameItem.getGame().getName(),
             cartGameItem.getGame().getPrice(),
             cartGameItem.getQuantityGameInCart());
   }
}
