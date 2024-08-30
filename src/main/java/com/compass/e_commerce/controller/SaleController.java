package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.game.GameListDto;
import com.compass.e_commerce.dto.sale.SaleRegistrationDto;
import com.compass.e_commerce.dto.sale.SaleReportListDto;
import com.compass.e_commerce.dto.sale.SaleReportRequestDto;
import com.compass.e_commerce.model.sale.Sale;
import com.compass.e_commerce.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/sale")
public class SaleController {
    @Autowired
    private SaleService saleService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> create(@RequestBody @Valid SaleRegistrationDto saleRegistrationDto) {
        Sale sale = saleService.convertDtoToEntity(saleRegistrationDto);
        saleService.create(sale);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/reportDay")
    public ResponseEntity <List<SaleReportListDto>> generationReportDay(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList = saleService.saleReportsDay(date).stream().map(SaleReportListDto::new).toList();

        if(!saleList.isEmpty()) {
            return ResponseEntity.ok().body(saleList);
        }
        return ResponseEntity.noContent().build();


    }

    @GetMapping("/reportWeek")
    public ResponseEntity<List<SaleReportListDto>> generationReportWeek(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList = saleService.saleReportsWeek(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            return ResponseEntity.ok().body(saleList);
        }
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/reportMonth")
    public ResponseEntity<List<SaleReportListDto>> generationReportMonth(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList = saleService.generateReportByMonth(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            return ResponseEntity.ok().body(saleList);
        }
        return ResponseEntity.noContent().build();

    }
}
