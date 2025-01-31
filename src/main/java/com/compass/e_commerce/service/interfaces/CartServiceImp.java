package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.dto.cart.AddGameToCartDto;
import com.compass.e_commerce.model.Cart;
import com.compass.e_commerce.model.Game;

public interface CartServiceImp {

    Cart create(Cart cart);
    void addGameToTheCart(Long id, int quantity);
}
