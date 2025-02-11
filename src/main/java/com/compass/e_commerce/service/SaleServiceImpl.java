package com.compass.e_commerce.service;

import com.compass.e_commerce.model.Sale;
import com.compass.e_commerce.repository.SaleRepository;
import com.compass.e_commerce.service.interfaces.CrudService;
import com.compass.e_commerce.service.interfaces.SaleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    @Transactional
    public Sale create(Sale entity) {
        return saleRepository.save(entity);
    }

    @Override
    public List<Sale> getAll() {
        return saleRepository.findAll();
    }

    @Override
    public Sale getById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale não encontrada com id: " + id));
    }

    @Override
    public List<Sale> detailedSaleReportsDay(LocalDate date) {
        return saleRepository.findByConfirmationDateAndStage(date);
    }

    @Override
    public List<Sale> detailedSaleReportsWeek(LocalDate date) {
        LocalDate firstDayOfWeek = date.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);

        return saleRepository.findByConfirmationDateBetweenAndStage(firstDayOfWeek, lastDayOfWeek);
    }

    @Override
    public List<Sale> detailedSaleReportsMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        return saleRepository.findByConfirmationDateBetweenAndStage(firstDayOfMonth, lastDayOfMonth);
    }
}
