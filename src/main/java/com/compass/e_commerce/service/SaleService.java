package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.sale.SaleRegistrationDto;
import com.compass.e_commerce.model.game.Game;
import com.compass.e_commerce.model.sale.Sale;
import com.compass.e_commerce.model.user.User;
import com.compass.e_commerce.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
    public class SaleService {

        @Autowired
        private UserService userService;
        @Autowired
        private GameService gameService;
        @Autowired
        private StockService stockService;

        private final SaleRepository saleRepository;


    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }


    public Sale convertDtoToEntity(SaleRegistrationDto dataDto) {
        Map<Game, Integer> games = new HashMap<>();
        Map<Long, Integer> mapDto = dataDto.games();

        for (Map.Entry<Long, Integer> entry : mapDto.entrySet()) {
            Optional<Game> game = gameService.getId(entry.getKey());
            if (game.isPresent()) {
                games.put(game.get(), entry.getValue());
                stockService.stockReduction(game.get(), entry.getValue());
            } else {
                throw new RuntimeException("Game not found");
            }
        }
        return new Sale(games);
    }

    public Sale create(Sale sale) {
        Long id = userService.getAuthenticatedUserId();
        Optional<User> user = userService.findById(id);
        sale.setDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        sale.setUser(user.get());

        return saleRepository.save(sale);
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

    public List<Sale> generateReportByMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        return saleRepository.findBySaleDateBetween(firstDayOfMonth, lastDayOfMonth);
    }

}
