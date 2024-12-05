package com.compass.e_commerce.dto.cart;

import java.util.List;

public record CartGameItemResponseDto(
        List<CartGameItemListDto> items,
        int quantityTotalOfItems,
        double totalPrice

) {
   public CartGameItemResponseDto(List<CartGameItemListDto> items, int quantityTotalOfItems, double totalPrice) {
        this.items = items;
        this.quantityTotalOfItems = quantityTotalOfItems;
        this.totalPrice = totalPrice;
   }
}
