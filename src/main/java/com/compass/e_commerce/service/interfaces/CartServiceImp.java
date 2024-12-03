package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.dto.cart.AddGameToCartDto;
import com.compass.e_commerce.model.Cart;

public interface CartServiceImp {

    Cart create(Cart cart);
    void addGameToTheCart(AddGameToCartDto addGameToCartDto);
}
