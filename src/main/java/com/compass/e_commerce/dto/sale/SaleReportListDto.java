package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.dto.order.OrderGameListDto;
import com.compass.e_commerce.model.Sale;
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
    private String userEmail;
    private Set<OrderGameListDto> games;

    public SaleReportListDto(Sale sale) {
        this(sale.getId(),
                sale.getPaymentDate(),
                sale.getOrder().getUser().getLogin(),
                sale.getOrder().getUser().getEmail(),
                sale.getOrder().getOrderGames().stream()
                        .map(OrderGameListDto::new)
                        .collect(Collectors.toSet()));
    }
}
