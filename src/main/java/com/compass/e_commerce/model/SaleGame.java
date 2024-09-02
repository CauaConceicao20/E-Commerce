package com.compass.e_commerce.model;

import com.compass.e_commerce.model.pk.SaleGamePK;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_sale_games")
public class SaleGame {

    @EmbeddedId
    SaleGamePK id = new SaleGamePK();

    private Integer quantity;

    public SaleGame(Sale sale, Game game, Integer quantity) {
        id.setSale(sale);
        id.setGame(game);
        this.quantity = quantity;
    }

    public Sale getSale() {
        return id.getSale();
    }

    public void setSale(Sale sale) {
        id.setSale(sale);
    }

    public Game getGame() {
        return id.getGame();
    }

    public void setGame(Game game) {
        id.setGame(game);
    }
}