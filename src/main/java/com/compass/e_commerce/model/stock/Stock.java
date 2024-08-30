package com.compass.e_commerce.model.stock;

import com.compass.e_commerce.dto.stock.StockDto;
import com.compass.e_commerce.model.game.Game;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_stock")
public class Stock {
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
        this.quantity = quantity - quantityReduction;
    }

    public void stockReplenishment(int quantityReplenishment) {
        if (this.quantity < quantityReplenishment || quantityReplenishment <= 0) {
            throw new IllegalArgumentException("Quantity replenishment must be positive.");
        }
        this.quantity = quantity + quantityReplenishment;
    }
}
