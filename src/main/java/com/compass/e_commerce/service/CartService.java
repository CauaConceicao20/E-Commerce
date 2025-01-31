package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.cart.AddGameToCartDto;
import com.compass.e_commerce.dto.cart.CartGameItemListDto;
import com.compass.e_commerce.dto.cart.CartGameItemResponseDto;
import com.compass.e_commerce.model.Cart;
import com.compass.e_commerce.model.CartGameItem;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.pk.CartGameItemPK;
import com.compass.e_commerce.repository.CartRepository;
import com.compass.e_commerce.service.interfaces.CartServiceImp;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartService implements CartServiceImp {

    private final CartRepository cartRepository;
    private final GameService gameService;
    private final UserService userService;

    @Transactional
    public Cart create(Cart cart) {
        return cartRepository.save(cart);
    }

    @Transactional
    public void addGameToTheCart(Long id, int quantity) {
        User authenticatedUser = userService.getById(userService.getAuthenticatedUserId());
        Cart authenticatedUserCart = authenticatedUser.getCart();
        Game game = gameService.getById(id);
        List<CartGameItem> cartGameRelatedToCart = authenticatedUserCart.getCartGameItem();

        boolean existingGame = false;
        for (CartGameItem cartGame : cartGameRelatedToCart) {
            if (cartGame.getGame().equals(game)) {
                existingGame = true;
                cartGame.setQuantityGameInCart(cartGame.getQuantityGameInCart() + quantity);
                authenticatedUserCart.setQuantityOfItems(authenticatedUserCart.getQuantityOfItems() + quantity);
                authenticatedUserCart.setTotalPrice(authenticatedUserCart.getTotalPrice() + game.getPrice() * quantity);
                break;
            }
        }
        if (!existingGame) {
            CartGameItem cartGameItem = new CartGameItem();
            cartGameItem.setId(new CartGameItemPK(authenticatedUserCart, game));
            authenticatedUserCart.getCartGameItem().add(cartGameItem);
            cartGameItem.setGame(game);
            cartGameItem.setQuantityGameInCart(cartGameItem.getQuantityGameInCart() + quantity);
            authenticatedUserCart.setQuantityOfItems(authenticatedUserCart.getQuantityOfItems() + quantity);
            authenticatedUserCart.setTotalPrice(authenticatedUserCart.getTotalPrice() + game.getPrice() * quantity);
        }
        cartRepository.save(authenticatedUserCart);
    }

    public void processGameAddition(AddGameToCartDto addGameToCartDto) {
        addGameToTheCart(addGameToCartDto.gameId(), addGameToCartDto.quantityGameInCart());
    }

    public CartGameItemResponseDto listGamesInTheCart() {
        User authenticatedUser = userService.getById(userService.getAuthenticatedUserId());
        Cart authenticatedUserCart = authenticatedUser.getCart();

        List<CartGameItemListDto> items = authenticatedUserCart.getCartGameItem().stream().map(CartGameItemListDto::new).toList();

        return new CartGameItemResponseDto(items, authenticatedUserCart.getQuantityOfItems(), authenticatedUserCart.getTotalPrice());
    }

    public boolean checkIfTheGameIsInTheCart(Long id) {
        User authenticatedUser = userService.getById(userService.getAuthenticatedUserId());
        Game game = gameService.getById(id);
        List<CartGameItem> gamesInCart = authenticatedUser.getCart().getCartGameItem();

        for (CartGameItem gamesOfCart : gamesInCart) {
            if (gamesOfCart.getGame().getId().equals(game.getId()))
                return true;
        }
        return false;
    }

    @Transactional
    public void removeGameFromCart(Long id) {
        User authenticatedUser = userService.getById(userService.getAuthenticatedUserId());
        Cart authenticatedUserCart = authenticatedUser.getCart();
        boolean cartGameItemRelationshipExists = false;

        List<CartGameItem> cartGameRelatedToCart = authenticatedUserCart.getCartGameItem();

        for (CartGameItem cartGameItem : cartGameRelatedToCart) {
            if (cartGameItem.getGame().getId().equals(id)) {
                cartGameItemRelationshipExists = true;

                authenticatedUserCart.setQuantityOfItems(authenticatedUserCart.getQuantityOfItems() - 1);
                authenticatedUserCart.setTotalPrice(authenticatedUserCart.getTotalPrice() - cartGameItem.getGame().getPrice());
                cartGameItem.setQuantityGameInCart(cartGameItem.getQuantityGameInCart() - 1);

                if (authenticatedUserCart.getQuantityOfItems() == 0) {
                    cartGameItem.getGame().getCartGameItem().remove(cartGameItem);
                }
                break;
            }
        }
        if (!cartGameItemRelationshipExists) {
            throw new EntityNotFoundException("Não existe game com esse id no carrinho");
        }
        cartRepository.save(authenticatedUserCart);
    }
}
