package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.order.OrderListDto;
import com.compass.e_commerce.dto.order.OrderRegistrationDto;
import com.compass.e_commerce.dto.order.OrderUpdateDto;
import com.compass.e_commerce.dto.order.SwapGameDto;
import com.compass.e_commerce.exception.personalized.DeletionNotAllowedException;
import com.compass.e_commerce.exception.personalized.SaleAlreadyConfirmedException;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.Order;
import com.compass.e_commerce.model.OrderGames;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.enums.Stage;
import com.compass.e_commerce.model.pk.OrderGamePK;
import com.compass.e_commerce.repository.OrderGameRepository;
import com.compass.e_commerce.repository.OrderRepository;
import com.compass.e_commerce.service.interfaces.SaleServiceImp;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements SaleServiceImp {

    private final UserService userService;
    private final GameService gameService;
    private final StockService stockService;
    private final OrderRepository orderRepository;
    private final OrderGameRepository orderGameRepository;

    public Order convertDtoToEntity(OrderRegistrationDto dataDto) {
        Order order = new Order();
        double totalPrice = 0.0;
        User user = userService.getById(userService.getAuthenticatedUserId());
        order.setUser(user);
        order.setCreationTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));



        for (OrderRegistrationDto.OrderGameRegistrationDto orderGameDto : dataDto.games()) {
            Game game = gameService.getById(orderGameDto.gameId());

            OrderGames orderGames = new OrderGames();
            orderGames.setId(new OrderGamePK(order, game));

            orderGames.setQuantity(orderGameDto.quantity());
            order.getOrderGames().add(orderGames);
            game.getOrderGames().add(orderGames);

            totalPrice += game.getPrice() * orderGameDto.quantity();
        }
        order.setStage(Stage.UNCONFIRMED);
        order.setTotalPrice(totalPrice);
        return order;
    }

    @CacheEvict(value = "orders", allEntries = true)
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    @Cacheable("orders")
    public List<OrderListDto> getAll() {
        return orderRepository.findAll().stream()
                .map(OrderListDto::new)
                .collect(Collectors.toList());
    }

    public Order getId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order não encontrada id :" + id));
        return order;
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public Order update(Long id, OrderUpdateDto orderUpdateDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Id da Order não existe"));

        if (order.getStage() == Stage.UNCONFIRMED) {
            for (OrderUpdateDto.OrderGameUpdateDto orderGameUpdateDto : orderUpdateDto.games()) {
                OrderGames existingOrderGames = order.getOrderGames().stream()
                        .filter(saleGame -> saleGame.getGame().getId().equals(orderGameUpdateDto.gameId()))
                        .findFirst()
                        .orElse(null);

                if (existingOrderGames == null) {
                    throw new NoSuchElementException("Id do game não existe");
                }
                stockService.adjustStockBasedOnSaleQuantityChange(existingOrderGames, orderGameUpdateDto.quantity());
                existingOrderGames.setQuantity(orderGameUpdateDto.quantity());
            }
        } else {
            throw new SaleAlreadyConfirmedException("Essa venda já foi confirmada");
        }
        double totalPrice = order.getOrderGames().stream()
                .mapToDouble(orderGame -> orderGame.getGame().getPrice() * orderGame.getQuantity())
                .sum();
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public Order confirmedOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não existe Order com esse id"));

        if (order.getStage() == Stage.CONFIRMED) {
            throw new SaleAlreadyConfirmedException("Essa venda já foi confirmada");
        }
        order.getOrderGames().forEach(orderGame -> {
            Game game = orderGame.getGame();
            int quantity = orderGame.getQuantity();
            stockService.stockReduction(game, quantity);
        });

        order.setStage(Stage.CONFIRMED);
        //order.setConfirmationTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        return orderRepository.save(order);
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public Order swapGame(SwapGameDto swapGameDto) {
        Order order = orderRepository.findById(swapGameDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Não existe Order com esse id"));

        if (order.getStage() == Stage.CONFIRMED) {
            throw new SaleAlreadyConfirmedException("Essa venda já foi confirmada");
        }

        for (SwapGameDto.SwapGameListDto swapGame : swapGameDto.swapGames()) {
            if (swapGame.currentGameId().equals(swapGame.newGameId())) {
                throw new IllegalArgumentException("O ID do novo jogo não pode ser igual ao ID do jogo atual.");
            }

            boolean newGameAlreadyInSale = order.getOrderGames().stream()
                    .anyMatch(orderGame -> orderGame.getGame().getId().equals(swapGame.newGameId()));
            if (newGameAlreadyInSale) {
                throw new IllegalArgumentException("O ID do novo jogo já existe na venda.");
            }

            OrderGames existingOrderGames = order.getOrderGames().stream()
                    .filter(orderGame -> orderGame.getGame().getId().equals(swapGame.currentGameId()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("ID do jogo não encontrado na venda"));

            /*
            Game oldGame = existingOrderGames.getGame();
            int quantity = existingOrderGames.getQuantity();
             */

            order.getOrderGames().remove(existingOrderGames);
            orderGameRepository.delete(existingOrderGames);
            orderRepository.flush();

            Game newGame = gameService.getById(swapGame.newGameId());

            if (swapGame.quantity() > newGame.getStock().getQuantity()) {
                throw new IllegalArgumentException("Quantidade do novo jogo excede o estoque disponível.");
            }

            OrderGames newOrderGames = new OrderGames();
            newOrderGames.setId(new OrderGamePK(order, newGame));
            newOrderGames.setQuantity(swapGame.quantity());

            order.getOrderGames().add(newOrderGames);
        }
        double totalPrice = order.getOrderGames().stream()
                .mapToDouble(orderGame -> orderGame.getId().getGame().getPrice() * orderGame.getQuantity())
                .sum();

        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    @CacheEvict(value = "orders", allEntries = true)
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não existe Order com esse id"));

        if (order.getStage() == Stage.CONFIRMED) {
            throw new DeletionNotAllowedException("A Venda já foi confirmada");
        }
        orderRepository.deleteById(id);
    }
}
