package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.order.*;
import com.compass.e_commerce.exception.personalized.DeletionNotAllowedException;
import com.compass.e_commerce.exception.personalized.SaleAlreadyConfirmedException;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.Order;
import com.compass.e_commerce.model.OrderGames;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.enums.Stage;
import com.compass.e_commerce.model.pk.OrderGamePK;
import com.compass.e_commerce.repository.SaleGameRepository;
import com.compass.e_commerce.repository.SaleRepository;
import com.compass.e_commerce.service.interfaces.SaleServiceImp;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService implements SaleServiceImp {

    private final UserService userService;
    private final GameService gameService;
    private final StockService stockService;
    private final SaleRepository saleRepository;
    private final SaleGameRepository saleGameRepository;

    public Order convertDtoToEntity(SaleRegistrationDto dataDto) {
        Order order = new Order();
        double totalPrice = 0.0;
        User user = userService.getById(userService.getAuthenticatedUserId());
        order.setUser(user);
        order.setCreationTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        for (SaleRegistrationDto.SaleGameRegistrationDto saleGameDto : dataDto.games()) {
            Game game = gameService.getById(saleGameDto.gameId());

            OrderGames orderGames = new OrderGames();
            orderGames.setId(new OrderGamePK(order, game));

            orderGames.setQuantity(saleGameDto.quantity());
            order.getOrderGames().add(orderGames);
            game.getOrderGames().add(orderGames);

            totalPrice += game.getPrice() * saleGameDto.quantity();
        }
        order.setStageOrder(Stage.UNCONFIRMED);
        order.setTotalPrice(totalPrice);
        return order;
    }

    @CacheEvict(value = "orders", allEntries = true)
    public Order create(Order order) {
        return saleRepository.save(order);
    }

    @Cacheable("orders")
    public List<SaleListDto> getAll() {
        return saleRepository.findAll().stream()
                .map(SaleListDto::new)
                .collect(Collectors.toList());
    }

    public Order getId(Long id) {
        Order order = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order não encontrada id :" + id));
        return order;
    }

    public List<Order> saleReportsDay(LocalDate date) {
        List<Order> list = saleRepository.findByConfirmationDateAndStage(date);
        return list;
    }

    public List<Order> saleReportsWeek(LocalDate date) {
        LocalDate firstDayOfWeek = date.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);

        List<Order> list = saleRepository.findByConfirmationDateBetweenAndStage(firstDayOfWeek, lastDayOfWeek);
        return list;
    }

    public List<Order> SaleReportsMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        List<Order> list = saleRepository.findByConfirmationDateBetweenAndStage(firstDayOfMonth, lastDayOfMonth);
        return list;
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public Order update(Long id, SaleUpdateDto saleUpdateDto) {
        Order order = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Id da Order não existe"));

        if (order.getStageOrder() == Stage.UNCONFIRMED) {
            for (SaleUpdateDto.SaleGameUpdateDto saleGameUpdateDto : saleUpdateDto.games()) {
                OrderGames existingOrderGames = order.getOrderGames().stream()
                        .filter(saleGame -> saleGame.getGame().getId().equals(saleGameUpdateDto.gameId()))
                        .findFirst()
                        .orElse(null);

                if (existingOrderGames == null) {
                    throw new NoSuchElementException("Id do game não existe");
                }
                stockService.adjustStockBasedOnSaleQuantityChange(existingOrderGames, saleGameUpdateDto.quantity());
                existingOrderGames.setQuantity(saleGameUpdateDto.quantity());
            }
        } else {
            throw new SaleAlreadyConfirmedException("Essa venda já foi confirmada");
        }
        double totalPrice = order.getOrderGames().stream()
                .mapToDouble(saleGame -> saleGame.getGame().getPrice() * saleGame.getQuantity())
                .sum();
        order.setTotalPrice(totalPrice);
        return saleRepository.save(order);
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public Order confirmedOrder(Long id) {
        Order order = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não existe Order com esse id"));

        if (order.getStageOrder() == Stage.CONFIRMED) {
            throw new SaleAlreadyConfirmedException("Essa venda já foi confirmada");
        }
        order.getOrderGames().forEach(saleGame -> {
            Game game = saleGame.getGame();
            int quantity = saleGame.getQuantity();
            stockService.stockReduction(game, quantity);
        });

        order.setStageOrder(Stage.CONFIRMED);
        order.setConfirmationTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        return saleRepository.save(order);
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public Order swapGame(SwapGameDto swapGameDto) {
        Order order = saleRepository.findById(swapGameDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Não existe Order com esse id"));

        if (order.getStageOrder() == Stage.CONFIRMED) {
            throw new SaleAlreadyConfirmedException("Essa venda já foi confirmada");
        }

        for (SwapGameDto.SwapGameListDto swapGame : swapGameDto.swapGames()) {
            if (swapGame.currentGameId().equals(swapGame.newGameId())) {
                throw new IllegalArgumentException("O ID do novo jogo não pode ser igual ao ID do jogo atual.");
            }

            boolean newGameAlreadyInSale = order.getOrderGames().stream()
                    .anyMatch(saleGame -> saleGame.getGame().getId().equals(swapGame.newGameId()));
            if (newGameAlreadyInSale) {
                throw new IllegalArgumentException("O ID do novo jogo já existe na venda.");
            }

            OrderGames existingOrderGames = order.getOrderGames().stream()
                    .filter(saleGame -> saleGame.getGame().getId().equals(swapGame.currentGameId()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("ID do jogo não encontrado na venda"));

            Game oldGame = existingOrderGames.getGame();
            int quantity = existingOrderGames.getQuantity();

            order.getOrderGames().remove(existingOrderGames);
            saleGameRepository.delete(existingOrderGames);
            saleRepository.flush();

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
                .mapToDouble(saleGame -> saleGame.getId().getGame().getPrice() * saleGame.getQuantity())
                .sum();

        order.setTotalPrice(totalPrice);
        return saleRepository.save(order);
    }

    @CacheEvict(value = "orders", allEntries = true)
    public void delete(Long id) {
        Order order = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não existe Order com esse id"));

        if (order.getStageOrder() == Stage.CONFIRMED) {
            throw new DeletionNotAllowedException("A Venda já foi confirmada");
        }
        saleRepository.deleteById(id);
    }
}
