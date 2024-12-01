package com.compass.e_commerce.dto.sale;

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
public class SaleListDto extends RepresentationModel<SaleListDto> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDateTime creationTimestamp;
    private LocalDateTime confirmationTimestamp;
    private String userName;
    private String userEmail;
    private Set<SaleGameListDto> games;
    private Double totalPrice;

    // Construtor para converter de Sale
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


