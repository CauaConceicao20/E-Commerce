package com.compass.e_commerce.dto.order;

import com.compass.e_commerce.model.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailsDto extends RepresentationModel<SaleDetailsDto> {
    private Long id;
    private LocalDateTime creationTimestamp;
    private LocalDateTime confirmationTimestamp;
    private Set<SaleGameListDto> games;
    private Double totalPrice;

    public SaleDetailsDto(Order order) {
        this(order.getId(),
                order.getCreationTimestamp(),
                order.getConfirmationTimestamp(),
                order.getOrderGames().stream()
                        .map(SaleGameListDto::new)
                        .collect(Collectors.toSet()),
                order.getTotalPrice());
    }
}
