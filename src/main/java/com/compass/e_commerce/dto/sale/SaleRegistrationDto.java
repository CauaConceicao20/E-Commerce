package com.compass.e_commerce.dto.sale;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record SaleRegistrationDto(
        @NotEmpty
        List<SaleGameDto> games
) {
    public static record SaleGameDto(
            @NotNull
            Long gameId,
            @Size(min = 1)
            Integer quantity
    ) {

    }
}
