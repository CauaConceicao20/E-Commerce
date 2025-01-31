package com.compass.e_commerce.model;

import com.compass.e_commerce.model.pk.OrderGamePK;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_order_games")
public class OrderGames implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    OrderGamePK id = new OrderGamePK();

    private Integer quantity;

    public OrderGames(Order order, Game game, Integer quantity) {
        id.setOrder(order);
        id.setGame(game);
        this.quantity = quantity;
    }

    public Order getOrder() {
        return id.getOrder();
    }

    public void setOrder(Order order) {
        id.setOrder(order);
    }

    public Game getGame() {
        return id.getGame();
    }

    public void setGame(Game game) {
        id.setGame(game);
    }

   @Override
    public String toString() {
        return "\ngame: " + id.getGame().getName() +
                "\ndescrição: " + id.getGame().getDescription() +
                "\nquantidade: " + quantity +
                "\npreço: " + id.getGame().getPrice() * quantity;
    }
}