package com.compass.e_commerce.dto.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RemoveGameFromCartDto(
        @Min(1)
        @Max(1)
        int quantity

) {
}
