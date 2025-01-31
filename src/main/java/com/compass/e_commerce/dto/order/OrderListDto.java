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
public class OrderListDto extends RepresentationModel<OrderListDto> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDateTime creationTimestamp;
    private String userName;
    private String userEmail;
    private Set<OrderGameListDto> games;

    // Construtor para converter de Order
    public OrderListDto(Order order) {
        this(order.getId(),
                order.getCreationTimestamp(),
                order.getUser().getLogin(),
                order.getUser().getEmail(),
                order.getOrderGames().stream()
                        .map(OrderGameListDto::new)
                        .collect(Collectors.toSet()));
    }
}


