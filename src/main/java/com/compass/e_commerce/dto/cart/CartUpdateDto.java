package com.compass.e_commerce.dto.cart;

public record CartUpdateDto(
        int quantityOfItems,
        double totalPrice) {
}
