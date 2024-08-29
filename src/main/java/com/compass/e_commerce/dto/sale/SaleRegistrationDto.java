package com.compass.e_commerce.dto.sale;

import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record SaleRegistrationDto(
        @NotEmpty
        Map<Long, Integer> games) {
}
