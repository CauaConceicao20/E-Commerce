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
public class OrderDetailsDto extends RepresentationModel<OrderDetailsDto> {
    private Long id;
    private LocalDateTime creationTimestamp;
    private Set<OrderGameListDto> games;

    public OrderDetailsDto(Order order) {
        this(order.getId(),
                order.getCreationTimestamp(),
                order.getOrderGames().stream()
                        .map(OrderGameListDto::new)
                        .collect(Collectors.toSet()));
    }
}
