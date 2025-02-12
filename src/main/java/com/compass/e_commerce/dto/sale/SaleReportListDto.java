package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.dto.order.OrderGameListDto;
import com.compass.e_commerce.model.Sale;
import com.compass.e_commerce.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleReportListDto extends RepresentationModel<SaleReportListDto> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long saleId;
    private LocalDateTime paymentDate;
    private String userName;
    private Set<OrderGameListDto> games;
    private PaymentMethod paymentMethod;
    private double totalPrice;

    public SaleReportListDto(Sale sale) {
        this(sale.getId(),
                sale.getPaymentDate(),
                sale.getOrder().getUser().getUsername(),
                sale.getOrder().getOrderGames().stream()
                        .map(OrderGameListDto::new)
                        .collect(Collectors.toSet()),
                sale.getOrder().getPaymentMethod(),
                sale.getOrder().getTotalPrice()
        );
    }
}
