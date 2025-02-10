package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.cart.AddGameToCartDto;
import com.compass.e_commerce.dto.cart.CartGameItemResponseDto;
import com.compass.e_commerce.service.CartServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartServiceImpl cartServiceImpl;

    @PostMapping("/v1/addGameInCart")
    public ResponseEntity<Void> addGameInCart(@RequestBody @Valid AddGameToCartDto addGameToCartDto) {
        cartServiceImpl.processGameAddition(addGameToCartDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/v1/ListGamesInCart")
    public ResponseEntity<CartGameItemResponseDto> viewCart() {
        return ResponseEntity.ok().body(cartServiceImpl.listGamesInTheCart());
    }

    @DeleteMapping("/v1/removeGameFromCart/{id}")
    public ResponseEntity<Void> removeGameFromCart(@PathVariable Long id) {
        cartServiceImpl.removeGameFromCart(id);

        return ResponseEntity.noContent().build();
    }

}
