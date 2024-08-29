package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.sale.SaleRegistrationDto;
import com.compass.e_commerce.model.sale.Sale;
import com.compass.e_commerce.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
