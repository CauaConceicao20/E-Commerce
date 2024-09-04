package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.model.Sale;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record SaleListDto(
        Long id,
        LocalDateTime creationTimestamp,
        LocalDateTime confirmationTimestamp,
        String userName,
        String userEmail,
        Set<SaleGameListDto> games,
        Double totalPrice
) implements Serializable {
    private static final long serialVersionUID = 1L;

    public SaleListDto(Sale sale) {
        this(sale.getId(),
                sale.getCreationTimestamp(),
                sale.getConfirmationTimestamp(),
                sale.getUser().getLogin(),
                sale.getUser().getEmail(),
                sale.getSaleGame().stream()
                        .map(SaleGameListDto::new)
                        .collect(Collectors.toSet()),
                sale.getTotalPrice());
    }
}


