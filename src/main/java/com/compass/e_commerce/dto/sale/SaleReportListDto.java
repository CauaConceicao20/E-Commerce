package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.model.Sale;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record SaleReportListDto(
        Long saleId,
        LocalDateTime saleDate,
        String userName,
        String userEmail,
        Set<SaleGameListDto> games

) implements Serializable {
    private static final long serialVersionUID = 1L;

    public SaleReportListDto(Sale sale) {
        this(sale.getId(),
                sale.getDateTime(),
                sale.getUser().getLogin(),
                sale.getUser().getEmail(),
                sale.getSaleGame().stream()
                        .map(SaleGameListDto::new)
                        .collect(Collectors.toSet()
                        ));
    }
}

