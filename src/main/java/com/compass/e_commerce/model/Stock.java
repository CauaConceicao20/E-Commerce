package com.compass.e_commerce.model;

import com.compass.e_commerce.dto.stock.StockDto;
import com.compass.e_commerce.exception.personalized.ExceededStockException;
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

    public  Stock(StockDto stockDto) {
        this.quantity = stockDto.quantity();
    }

    public void stockReduction(int quantityReduction) {
        if (this.quantity < quantityReduction || quantityReduction <= 0) {
            throw new ExceededStockException("O estoque não pode ficar negativo");
        }
        this.quantity -= quantityReduction;
    }

    public void stockReplenishment(int quantityReplenishment) {
        if (quantityReplenishment <= 0) {
            throw new ExceededStockException("O reabastecimento de quantidade deve ser positivo.");
        }
        this.quantity += quantityReplenishment;
    }
}
