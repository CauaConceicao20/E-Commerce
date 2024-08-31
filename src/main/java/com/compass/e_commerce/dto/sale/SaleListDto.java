package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.model.Sale;
import java.time.LocalDateTime;

public record SaleListDto(
        Long id,
        LocalDateTime date,
        String userName,
        String userEmail

) {

    public SaleListDto(Sale sale) {
        this(sale.getId(),
                sale.getDateTime(),
                sale.getUser().getLogin(),
                sale.getUser().getEmail()
        );
    }
}


