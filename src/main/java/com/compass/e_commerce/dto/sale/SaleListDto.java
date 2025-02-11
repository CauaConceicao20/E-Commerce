package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.model.Order;
import com.compass.e_commerce.model.Sale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleListDto extends RepresentationModel<SaleListDto> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDateTime paymentDate;
    private Long orderId;

    public SaleListDto(Sale sale) {
        this(sale.getId(),
                sale.getPaymentDate(),
                sale.getOrder().getId());
    }
}
