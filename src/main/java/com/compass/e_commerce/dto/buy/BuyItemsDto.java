package com.compass.e_commerce.dto.buy;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record BuyItemsDto(
        Set<PurchaseItems> purchaseItems) {

    public static record PurchaseItems(
            @NotNull
            Long id,
            @NotNull
            int quantity) {
    }
}
