package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.dto.game.GameDto;

import java.time.LocalDateTime;
import java.util.List;

public record SaleReportListDto(
        Long saleId,
        LocalDateTime saleDate,
        String userName,
        List<GameDto> products

) {
}

