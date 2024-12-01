package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.sale.*;
import com.compass.e_commerce.exception.personalized.DeletionNotAllowedException;
import com.compass.e_commerce.exception.personalized.SaleAlreadyConfirmedException;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.Sale;
import com.compass.e_commerce.model.SaleGame;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.enums.StageSale;
import com.compass.e_commerce.model.pk.SaleGamePK;
import com.compass.e_commerce.repository.SaleGameRepository;
import com.compass.e_commerce.repository.SaleRepository;
import com.compass.e_commerce.service.interfaces.SaleServiceInterface;
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
public class SaleService implements SaleServiceInterface {

    private final UserService userService;
    private final GameService gameService;
    private final StockService stockService;
    private final SaleRepository saleRepository;
    private final SaleGameRepository saleGameRepository;

    public Sale convertDtoToEntity(SaleRegistrationDto dataDto) {
        Sale sale = new Sale();
        double totalPrice = 0.0;
        User user = userService.getById(userService.getAuthenticatedUserId());
        sale.setUser(user);
        sale.setCreationTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        for (SaleRegistrationDto.SaleGameRegistrationDto saleGameDto : dataDto.games()) {
            Game game = gameService.getById(saleGameDto.gameId());

            SaleGame saleGame = new SaleGame();
            saleGame.setId(new SaleGamePK(sale, game));

            saleGame.setQuantity(saleGameDto.quantity());
            sale.getSaleGame().add(saleGame);
            game.getSaleGame().add(saleGame);

            totalPrice += game.getPrice() * saleGameDto.quantity();
        }
        sale.setStageSale(StageSale.UNCONFIRMED);
        sale.setTotalPrice(totalPrice);
        return sale;
    }

    @CacheEvict(value = "sales", allEntries = true)
    public Sale create(Sale sale) {
        return saleRepository.save(sale);
    }

    @Cacheable("sales")
    public List<SaleListDto> getAll() {
        return saleRepository.findAll().stream()
                .map(SaleListDto::new)
                .collect(Collectors.toList());
    }

    public Sale getId(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale não encontrada id :" + id));
        return sale;
    }

    public List<Sale> saleReportsDay(LocalDate date) {
        List<Sale> list = saleRepository.findByConfirmationDateAndStage(date);
        return list;
    }

    public List<Sale> saleReportsWeek(LocalDate date) {
        LocalDate firstDayOfWeek = date.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);

        List<Sale> list = saleRepository.findByConfirmationDateBetweenAndStage(firstDayOfWeek, lastDayOfWeek);
        return list;
    }

    public List<Sale> saleReportsMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        List<Sale> list = saleRepository.findByConfirmationDateBetweenAndStage(firstDayOfMonth, lastDayOfMonth);
        return list;
    }

    @Transactional
    @CacheEvict(value = "sales", allEntries = true)
    public Sale update(Long id, SaleUpdateDto saleUpdateDto) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Id da Sale não existe"));

        if (sale.getStageSale() == StageSale.UNCONFIRMED) {
            for (SaleUpdateDto.SaleGameUpdateDto saleGameUpdateDto : saleUpdateDto.games()) {
                SaleGame existingSaleGame = sale.getSaleGame().stream()
                        .filter(saleGame -> saleGame.getGame().getId().equals(saleGameUpdateDto.gameId()))
                        .findFirst()
                        .orElse(null);

                if (existingSaleGame == null) {
                    throw new NoSuchElementException("Id do game não existe");
                }
                stockService.adjustStockBasedOnSaleQuantityChange(existingSaleGame, saleGameUpdateDto.quantity());
                existingSaleGame.setQuantity(saleGameUpdateDto.quantity());
            }
        } else {
            throw new SaleAlreadyConfirmedException("Essa venda já foi confirmada");
        }
        double totalPrice = sale.getSaleGame().stream()
                .mapToDouble(saleGame -> saleGame.getGame().getPrice() * saleGame.getQuantity())
                .sum();
        sale.setTotalPrice(totalPrice);
        return saleRepository.save(sale);
    }

    @Transactional
    @CacheEvict(value = "sales", allEntries = true)
    public Sale confirmedSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não existe Sale com esse id"));

        if (sale.getStageSale() == StageSale.CONFIRMED) {
            throw new SaleAlreadyConfirmedException("Essa venda já foi confirmada");
        }
        sale.getSaleGame().forEach(saleGame -> {
            Game game = saleGame.getGame();
            int quantity = saleGame.getQuantity();
            stockService.stockReduction(game, quantity);
        });

        sale.setStageSale(StageSale.CONFIRMED);
        sale.setConfirmationTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        return saleRepository.save(sale);
    }

    @Transactional
    @CacheEvict(value = "sales", allEntries = true)
    public Sale swapGame(SwapGameDto swapGameDto) {
        Sale sale = saleRepository.findById(swapGameDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Não existe Sale com esse id"));

        if (sale.getStageSale() == StageSale.CONFIRMED) {
            throw new SaleAlreadyConfirmedException("Essa venda já foi confirmada");
        }

        for (SwapGameDto.SwapGameListDto swapGame : swapGameDto.swapGames()) {
            if (swapGame.currentGameId().equals(swapGame.newGameId())) {
                throw new IllegalArgumentException("O ID do novo jogo não pode ser igual ao ID do jogo atual.");
            }

            boolean newGameAlreadyInSale = sale.getSaleGame().stream()
                    .anyMatch(saleGame -> saleGame.getGame().getId().equals(swapGame.newGameId()));
            if (newGameAlreadyInSale) {
                throw new IllegalArgumentException("O ID do novo jogo já existe na venda.");
            }

            SaleGame existingSaleGame = sale.getSaleGame().stream()
                    .filter(saleGame -> saleGame.getGame().getId().equals(swapGame.currentGameId()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("ID do jogo não encontrado na venda"));

            Game oldGame = existingSaleGame.getGame();
            int quantity = existingSaleGame.getQuantity();

            sale.getSaleGame().remove(existingSaleGame);
            saleGameRepository.delete(existingSaleGame);
            saleRepository.flush();

            Game newGame = gameService.getById(swapGame.newGameId());

            if (swapGame.quantity() > newGame.getStock().getQuantity()) {
                throw new IllegalArgumentException("Quantidade do novo jogo excede o estoque disponível.");
            }

            SaleGame newSaleGame = new SaleGame();
            newSaleGame.setId(new SaleGamePK(sale, newGame));
            newSaleGame.setQuantity(swapGame.quantity());

            sale.getSaleGame().add(newSaleGame);
        }
        double totalPrice = sale.getSaleGame().stream()
                .mapToDouble(saleGame -> saleGame.getId().getGame().getPrice() * saleGame.getQuantity())
                .sum();

        sale.setTotalPrice(totalPrice);
        return saleRepository.save(sale);
    }

    @CacheEvict(value = "sales", allEntries = true)
    public void delete(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não existe Sale com esse id"));

        if (sale.getStageSale() == StageSale.CONFIRMED) {
            throw new DeletionNotAllowedException("A Venda já foi confirmada");
        }
        saleRepository.deleteById(id);
    }
}
