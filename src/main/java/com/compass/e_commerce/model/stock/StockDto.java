package com.compass.e_commerce.model.stock;

import jakarta.validation.constraints.Min;

public record StockDto(
        @Min(1)
        int quantity
) {
}
