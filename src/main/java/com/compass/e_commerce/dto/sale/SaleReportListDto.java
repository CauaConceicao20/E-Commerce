package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.model.game.Game;
import com.compass.e_commerce.model.sale.Sale;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record SaleReportListDto(
        Long id,
        LocalDateTime date,
        String nameUser,
        String emailUser,
        Map<String, Map<Integer,Double>> games


) {
    public SaleReportListDto(Sale sale) {
        this(sale.getId(),
                sale.getDateTime(),
                sale.getUser().getLogin(),
                sale.getUser().getEmail(),
                sale.returnGameName()
        );
    }
}

