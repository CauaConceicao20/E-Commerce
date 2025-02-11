package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.model.User;

public interface CartService<T, AD, CG> {

    void associateCartWithUser(User user);

    void addGameToTheCart(Long id, int quantity);

    void processGameAddition(AD addGameToCartDto);

    CG listGamesInTheCart();

    boolean checkIfTheGameIsInTheCart(Long id);

    void removeGameFromCart(Long id);

}
