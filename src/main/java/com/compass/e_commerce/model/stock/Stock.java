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
@EqualsAndHashCode
@Entity
@Table(name = "tb_stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    private int quantity;

    @OneToOne(mappedBy = "stock")
    private Game game;

    public Stock(StockDto stockDto) {
        this.quantity = stockDto.quantity();
    }

}
