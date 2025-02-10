package com.compass.e_commerce.service;

import com.compass.e_commerce.model.Sale;
import com.compass.e_commerce.repository.SaleRepository;
import com.compass.e_commerce.service.interfaces.CrudService;
import com.compass.e_commerce.service.interfaces.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements CrudService<Sale>, SaleService<Sale> {

    private final SaleRepository saleRepository;

    @Override
    public Sale create(Sale entity) {
        return null;
    }

    @Override
    public List<Sale> getAll() {
        return List.of();
    }

    @Override
    public Sale getById(Long id) {
        return null;
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
}
