package com.compass.e_commerce.dto.order;

import com.compass.e_commerce.model.Order;
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
    private LocalDateTime creationTimestamp;
    private LocalDateTime confirmationTimestamp;
    private String userName;
    private String userEmail;
    private Set<SaleGameListDto> games;

    public SaleReportListDto(Order order) {
        this(order.getId(),
                order.getCreationTimestamp(),
                order.getConfirmationTimestamp(),
                order.getUser().getLogin(),
                order.getUser().getEmail(),
                order.getOrderGames().stream()
                        .map(SaleGameListDto::new)
                        .collect(Collectors.toSet()));
    }
}
