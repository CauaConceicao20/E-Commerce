package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.model.Sale;

import java.time.LocalDateTime;;
import java.util.Set;
import java.util.stream.Collectors;

public record SaleDetailsDto(
        Long id,

        LocalDateTime getCreationTimestamp,

        LocalDateTime confirmationTimestamp,
        Set<SaleGameListDto> games,

        Double totalPrice

){
     public SaleDetailsDto(Sale sale) {
         this(sale.getId(),
                 sale.getCreationTimestamp(),
                 sale.getConfirmationTimestamp(),
                 sale.getSaleGame().stream()
                         .map(SaleGameListDto::new)
                         .collect(Collectors.toSet()),
                 sale.getTotalPrice());

     }
}
