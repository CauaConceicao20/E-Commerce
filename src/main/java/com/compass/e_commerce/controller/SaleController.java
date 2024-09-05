package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.sale.*;
import com.compass.e_commerce.model.Sale;
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

@RestController
@RequestMapping("/sale")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> create(@RequestBody @Valid SaleRegistrationDto saleRegistrationDto) {
        Sale sale = saleService.convertDtoToEntity(saleRegistrationDto);
        saleService.create(sale);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<SaleListDto>> list() {
        var saleList = saleService.list();
        return ResponseEntity.ok().body(saleList);
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<SaleDetailsDto> confirmSale(@PathVariable Long id) {
        Sale sale = saleService.confirmedSale(id);
        return ResponseEntity.ok().body(new SaleDetailsDto(sale));
    }

    @GetMapping("/reportDay")
    public ResponseEntity <List<SaleReportListDto>> generationReportDay(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleService.saleReportsDay(date).stream().map(SaleReportListDto::new).toList();

        if(!saleList.isEmpty()) {
            return ResponseEntity.ok().body(saleList);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reportWeek")
    public ResponseEntity<List<SaleReportListDto>> generationReportWeek(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleService.saleReportsWeek(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            return ResponseEntity.ok().body(saleList);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reportMonth")
    public ResponseEntity<List<SaleReportListDto>> generationReportMonth(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleService.saleReportsMonth(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            return ResponseEntity.ok().body(saleList);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update")
    public ResponseEntity<SaleDetailsDto> updateSale(@RequestBody @Valid SaleUpdateDto saleUpdateDto) {
        var sale = saleService.update(saleUpdateDto);
        return ResponseEntity.ok().body(new SaleDetailsDto(sale));
    }

    @PutMapping("/swap")
    public ResponseEntity<SaleDetailsDto> swapGame(@RequestBody @Valid SwapGameDto swapGameDto) {
        Sale sale = saleService.swapGame(swapGameDto);
        return ResponseEntity.ok().body(new SaleDetailsDto(sale));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        saleService.delete(id);
        return ResponseEntity.ok().build();
    }


}


