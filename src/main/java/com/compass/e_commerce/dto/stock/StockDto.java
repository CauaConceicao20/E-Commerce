package com.compass.e_commerce.dto.stock;

import jakarta.validation.constraints.Min;

public record StockDto(
        @Min(1)
        int quantity
) {
}
