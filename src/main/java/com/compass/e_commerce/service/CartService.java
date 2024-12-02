package com.compass.e_commerce.service;

import com.compass.e_commerce.model.Cart;
import com.compass.e_commerce.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public Cart create(Cart cart) {
        return cartRepository.save(cart);
    }
}
