package com.compass.e_commerce.service.interfaces;

public interface CartService<T, AD, CG> {
    void addGameToTheCart(Long id, int quantity);

    void processGameAddition(AD addGameToCartDto);

    CG listGamesInTheCart();

    boolean checkIfTheGameIsInTheCart(Long id);

    void removeGameFromCart(Long id);

}
