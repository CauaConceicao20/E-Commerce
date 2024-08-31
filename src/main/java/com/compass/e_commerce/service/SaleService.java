package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.game.GameDto;
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
    private final SaleGameRepository saleGameRepository;


    public SaleService(SaleRepository saleRepository, SaleGameRepository saleGameRepository) {
        this.saleRepository = saleRepository;
        this.saleGameRepository = saleGameRepository;
    }

    public Sale convertDtoToEntity(SaleRegistrationDto dataDto) {
        Sale sale = new Sale();
        User user = userService.findById(userService.getAuthenticatedUserId()).orElseThrow(() -> new RuntimeException("User Not found"));
        sale.setUser(user);
        sale.setDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));


        for (SaleRegistrationDto.SaleGameDto saleGameDto : dataDto.games()) {
            Game game = gameService.getId(saleGameDto.gameId())
                    .orElseThrow(() -> new RuntimeException("Game not found"));

            SaleGame saleGame = new SaleGame();
            saleGame.setId(new SaleGamePK(sale, game));
            saleGame.setQuantity(saleGameDto.quantity());
            stockService.stockReduction(game, saleGameDto.quantity());

            sale.getSalegame().add(saleGame);
            game.getSaleGame().add(saleGame);
        }
        return sale;
    }


    @CacheEvict(value = "games", allEntries = true)
    public Sale create(Sale sale) {
        return saleRepository.save(sale);
    }
    @Cacheable("sales")
    public List<Sale> list() {
        return saleRepository.findAll();
    }

    public LocalDateTime formaterDate(LocalDate date) {
        LocalDateTime dateDay = date.atStartOfDay();
        return dateDay;
    }

    public List<SaleReportListDto> getSalesWithProducts(List<Sale> sales) {
        return convertToSaleWithProductsDTO(sales);
    }

    public List<SaleReportListDto> convertToSaleWithProductsDTO(List<Sale> sales) {
        return sales.stream().map(sale -> {
            List<GameDto> gameDtos = sale.getSalegame().stream()
                    .map(saleGame -> new GameDto(
                            saleGame.getGame().getId(),
                            saleGame.getGame().getName(),
                            saleGame.getQuantity(),
                            saleGame.getGame().getPrice()
                    ))
                    .collect(Collectors.toList());

            return new SaleReportListDto(
                    sale.getId(),
                    sale.getDateTime(),
                    sale.getUser().getLogin(),
                    gameDtos
            );
        }).collect(Collectors.toList());
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
