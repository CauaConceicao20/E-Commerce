package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.cart.AddGameToCartDto;
import com.compass.e_commerce.repository.CartRepository;
import com.compass.e_commerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/v1/addGameInCart")
    public ResponseEntity<Void> addGameInCart(@RequestBody @Valid AddGameToCartDto addGameToCartDto) {
        cartService.addGameToTheCart(addGameToCartDto);
        return ResponseEntity.ok().build();
    }

}
