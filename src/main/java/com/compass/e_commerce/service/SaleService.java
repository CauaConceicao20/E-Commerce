package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.sale.SaleListDto;
import com.compass.e_commerce.dto.sale.SaleRegistrationDto;
import com.compass.e_commerce.dto.sale.SaleReportListDto;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.Sale;
import com.compass.e_commerce.model.SaleGame;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.pk.SaleGamePK;
import com.compass.e_commerce.repository.SaleGameRepository;
import com.compass.e_commerce.repository.SaleRepository;
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


    public SaleService(SaleRepository saleRepository, SaleGameRepository saleGameRepository) {
        this.saleRepository = saleRepository;
    }

    public Sale convertDtoToEntity(SaleRegistrationDto dataDto) {
        Sale sale = new Sale();
        double totalPrice = 0.0;
        User user = userService.findById(userService.getAuthenticatedUserId()).orElseThrow(() -> new RuntimeException("User Not found"));
        sale.setUser(user);
        sale.setDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        for (SaleRegistrationDto.SaleGameRegistrationDto saleGameDto : dataDto.games()) {
            Game game = gameService.getId(saleGameDto.gameId())
                    .orElseThrow(() -> new RuntimeException("Game not found"));

            SaleGame saleGame = new SaleGame();
            saleGame.setId(new SaleGamePK(sale, game));
            stockService.stockReduction(game, saleGameDto.quantity());

            saleGame.setQuantity(saleGameDto.quantity());
            sale.getSaleGame().add(saleGame);
            game.getSaleGame().add(saleGame);

            totalPrice += game.getPrice() * saleGameDto.quantity();
        }
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
        return saleRepository.findByDateTime(date);
    }

    public List<Sale> saleReportsWeek(LocalDate date) {
        LocalDate firstDayOfWeek = date.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);

        return saleRepository.findBySaleDateBetween(firstDayOfWeek, lastDayOfWeek);
    }

    public List<Sale> saleReportsMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        return saleRepository.findBySaleDateBetween(firstDayOfMonth, lastDayOfMonth);
    }

}
