package com.compass.e_commerce.model;

import com.compass.e_commerce.dto.stock.StockDto;
import com.compass.e_commerce.model.Game;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_stock")
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(0)
    private int quantity;

    @OneToOne(mappedBy = "stock")
    private Game game;

    public Stock(StockDto stockDto) {
        this.quantity = stockDto.quantity();
    }

    public void stockReduction(int quantityReduction) {
        if (this.quantity < quantityReduction || quantityReduction <= 0) {
            throw new IllegalArgumentException("Quantity reduction must be positive and cannot exceed the available stock.");
        }
        this.quantity -= quantityReduction;
    }

    public void stockReplenishment(int quantityReplenishment) {
        if (quantityReplenishment <= 0) {
            throw new IllegalArgumentException("Quantity replenishment must be positive.");
        }
        this.quantity += quantityReplenishment;
    }
}
