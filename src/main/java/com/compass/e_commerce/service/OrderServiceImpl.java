package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.order.OrderRegistrationDto;
import com.compass.e_commerce.exception.personalized.SaleAlreadyConfirmedException;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.Order;
import com.compass.e_commerce.model.OrderGames;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.enums.Stage;
import com.compass.e_commerce.model.pk.OrderGamePK;
import com.compass.e_commerce.repository.OrderRepository;
import com.compass.e_commerce.service.interfaces.CrudService;
import com.compass.e_commerce.service.interfaces.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements CrudService<Order>, OrderService<Order, OrderRegistrationDto> {

    private final UserServiceImpl userServiceImpl;
    private final GameServiceImpl gameServiceImpl;
    private final AuthenticationServiceImpl authenticationServiceImpl;
    private final StockServiceImpl stockServiceImpl;
    private final OrderRepository orderRepository;

    @Override
    public Order convertDtoToEntity(OrderRegistrationDto dataDto) {
        Order order = new Order();
        double totalPrice = 0.0;
        User user = userServiceImpl.getById(authenticationServiceImpl.getAuthenticatedUserId());
        order.setUser(user);
        order.setCreationTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        for (OrderRegistrationDto.OrderGameRegistrationDto orderGameDto : dataDto.games()) {
            Game game = gameServiceImpl.getById(orderGameDto.gameId());

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

    @Override
    @CacheEvict(value = "orders", allEntries = true)
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Cacheable("orders")
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order com encontrada id :" + id));
    }

    @Override
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
            stockServiceImpl.stockReduction(game, quantity);
        });

        order.setStage(Stage.CONFIRMED);

        return orderRepository.save(order);
    }
}
