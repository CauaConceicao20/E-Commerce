package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.sale.*;
import com.compass.e_commerce.exception.SaleAlreadyConfirmedException;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.Sale;
import com.compass.e_commerce.model.SaleGame;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.enums.StageSale;
import com.compass.e_commerce.model.pk.SaleGamePK;
import com.compass.e_commerce.repository.SaleGameRepository;
import com.compass.e_commerce.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SaleService {

    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;
    @Autowired
    private StockService stockService;

    private final SaleRepository saleRepository;

    private final SaleGameRepository saleGameRepository;

    public SaleService(SaleRepository saleRepository, SaleGameRepository saleGameRepository) {
        this.saleRepository = saleRepository;
        this.saleGameRepository = saleGameRepository;
    }

    public Sale convertDtoToEntity(SaleRegistrationDto dataDto) {
        Sale sale = new Sale();
        double totalPrice = 0.0;
        User user = userService.findById(userService.getAuthenticatedUserId())
                .orElseThrow(() -> new RuntimeException("Não existe User com esse id"));
        sale.setUser(user);
        sale.setCreationTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        for (SaleRegistrationDto.SaleGameRegistrationDto saleGameDto : dataDto.games()) {
            Game game = gameService.getId(saleGameDto.gameId())
                    .orElseThrow(() -> new RuntimeException("Não existe Game com esse id"));

            SaleGame saleGame = new SaleGame();
            saleGame.setId(new SaleGamePK(sale, game));
            stockService.stockReduction(game, saleGameDto.quantity());

            saleGame.setQuantity(saleGameDto.quantity());
            sale.getSaleGame().add(saleGame);
            game.getSaleGame().add(saleGame);

            totalPrice += game.getPrice() * saleGameDto.quantity();
        }
        sale.setStageSale(StageSale.UNCONFIRMED);
        sale.setTotalPrice(totalPrice);
        return sale;
    }

    @CacheEvict(value = "games", allEntries = true)
    public Sale create(Sale sale) {
        return saleRepository.save(sale);
    }

    @Cacheable("sales")
    public List<SaleListDto> list() {
        return saleRepository.findAll().stream()
                .map(SaleListDto::new)
                .collect(Collectors.toList());
    }

    public LocalDateTime formaterDate(LocalDate date) {
        LocalDateTime dateDay = date.atStartOfDay();
        return dateDay;
    }

    public List<Sale> saleReportsDay(LocalDate date) {
        return saleRepository.findByConfirmationDateAndStage(date);
    }

    public List<Sale> saleReportsWeek(LocalDate date) {
        LocalDate firstDayOfWeek = date.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);

        return saleRepository.findByConfirmationDateBetweenAndStage(firstDayOfWeek, lastDayOfWeek);
    }

    public List<Sale> saleReportsMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        return saleRepository.findByConfirmationDateBetweenAndStage(firstDayOfMonth, lastDayOfMonth);
    }

    public Sale update(SaleUpdateDto saleUpdateDto) {
        Sale sale = saleRepository.findById(saleUpdateDto.saleId())
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

    public Sale confirmedSale(Long id) {
        Sale sale = saleRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Não existe Sale com esse id"));

        if (sale.getStageSale() == StageSale.CONFIRMED)
            throw new SaleAlreadyConfirmedException("Essa venda já foi confirmada");

        sale.setStageSale(StageSale.CONFIRMED);
        sale.setConfirmationTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        return saleRepository.save(sale);
    }
    @Transactional
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

            stockService.stockReposition(oldGame, quantity);

            sale.getSaleGame().remove(existingSaleGame);
            saleGameRepository.delete(existingSaleGame);

            Game newGame = gameService.getId(swapGame.newGameId())
                    .orElseThrow(() -> new EntityNotFoundException("Novo jogo não encontrado"));

            stockService.stockReduction(newGame, swapGame.quantity());

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

    public void delete(Long id) {
        saleRepository.deleteById(id);
    }
}
