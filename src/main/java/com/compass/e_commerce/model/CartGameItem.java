package com.compass.e_commerce.model;

import com.compass.e_commerce.model.pk.CartGameItemPK;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_cart_games")
public class CartGameItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    CartGameItemPK id = new CartGameItemPK();

    private int quantityGameInCart;

    public CartGameItem(Game game, Cart cart, int quantityGameInCart) {
        id.setCart(cart);
        id.setGame(game);
        this.quantityGameInCart = quantityGameInCart;
    }

    public Game getGame() {
        return id.getGame();
    }

    public void setGame(Game game) {
        id.setGame(game);
    }

    public Cart getCart() {
        return id.getCart();
    }

    public void setCart(Cart cart) {
        id.setCart(cart);
    }

}
