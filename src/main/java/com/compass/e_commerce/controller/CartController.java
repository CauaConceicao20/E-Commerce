package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.cart.AddGameToCartDto;
import com.compass.e_commerce.dto.cart.CartGameItemListDto;
import com.compass.e_commerce.dto.cart.CartGameItemResponseDto;
import com.compass.e_commerce.dto.game.GameListDto;
import com.compass.e_commerce.repository.CartRepository;
import com.compass.e_commerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/v1/addGameInCart")
    public ResponseEntity<Void> addGameInCart(@RequestBody @Valid AddGameToCartDto addGameToCartDto) {
        cartService.addGameToTheCart(addGameToCartDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/v1/ListGamesInCart")
    public ResponseEntity<CartGameItemResponseDto> viewCart() {
        return ResponseEntity.ok().body(cartService.listGamesInTheCart());
    }

    @DeleteMapping("/v1/removeGameFromCart/{id}")
    public ResponseEntity<Void> removeGameFromCart(@PathVariable Long id) {
        cartService.removeGameFromCart(id);

        return ResponseEntity.noContent().build();
    }

}
