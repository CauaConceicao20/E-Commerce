package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.cart.AddGameToCartDto;
import com.compass.e_commerce.dto.cart.CartGameItemListDto;
import com.compass.e_commerce.dto.cart.CartGameItemResponseDto;
import com.compass.e_commerce.dto.cart.CartUpdateDto;
import com.compass.e_commerce.model.Cart;
import com.compass.e_commerce.model.CartGameItem;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.pk.CartGameItemPK;
import com.compass.e_commerce.repository.CartRepository;
import com.compass.e_commerce.service.interfaces.CartService;
import com.compass.e_commerce.service.interfaces.CrudService;
import com.compass.e_commerce.service.interfaces.OptionalCrudMethods;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CrudService<Cart>, OptionalCrudMethods<Cart, CartUpdateDto>,
        CartService<Cart, AddGameToCartDto, CartGameItemResponseDto> {

    private final CartRepository cartRepository;
    private final GameServiceImpl gameServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final AuthenticationServiceImpl authenticationServiceImpl;

    @Override
    @Transactional
    public Cart create(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getAll() {
        return cartRepository.findAll();
    }

    @Override
    public Cart getById(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não existe um carrinho com id: " + id));
    }

    @Override
    public void associateCartWithUser(User user) {
        Cart cart = new Cart();
        create(cart);
        user.setCart(cart);
    }

    @Override
    @Transactional
    public Cart update(Long id, CartUpdateDto cartUpdateDto) {
        Cart cart = getById(id);

        if (cartUpdateDto.quantityOfItems() > 0) {
            cart.setQuantityOfItems(cartUpdateDto.quantityOfItems());
        }
        if (cart.getTotalPrice() > 0) {
            cart.setTotalPrice(cartUpdateDto.totalPrice());
        }
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void addGameToTheCart(Long id, int quantity) {
        User authenticatedUser = userServiceImpl.getById(authenticationServiceImpl.getAuthenticatedUserId());
        Cart authenticatedUserCart = authenticatedUser.getCart();
        Game game = gameServiceImpl.getById(id);
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

    @Override
    public void processGameAddition(AddGameToCartDto addGameToCartDto) {
        addGameToTheCart(addGameToCartDto.gameId(), addGameToCartDto.quantityGameInCart());
    }

    @Override
    public CartGameItemResponseDto listGamesInTheCart() {
        User authenticatedUser = userServiceImpl.getById(authenticationServiceImpl.getAuthenticatedUserId());
        Cart authenticatedUserCart = authenticatedUser.getCart();

        List<CartGameItemListDto> items = authenticatedUserCart.getCartGameItem().stream().map(CartGameItemListDto::new).toList();

        return new CartGameItemResponseDto(items, authenticatedUserCart.getQuantityOfItems(), authenticatedUserCart.getTotalPrice());
    }

    @Override
    public boolean checkIfTheGameIsInTheCart(Long id) {
        User authenticatedUser = userServiceImpl.getById(authenticationServiceImpl.getAuthenticatedUserId());
        Game game = gameServiceImpl.getById(id);
        List<CartGameItem> gamesInCart = authenticatedUser.getCart().getCartGameItem();

        for (CartGameItem gamesOfCart : gamesInCart) {
            if (gamesOfCart.getGame().getId().equals(game.getId()))
                return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void removeGameFromCart(Long id) {
        User authenticatedUser = userServiceImpl.getById(authenticationServiceImpl.getAuthenticatedUserId());
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
