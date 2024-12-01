package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.model.Sale;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
