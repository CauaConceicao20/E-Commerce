package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.buy.BuyItemsDto;
import com.compass.e_commerce.service.PurchasingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/purchasing")
@RequiredArgsConstructor
public class PurchasingController {

    private final PurchasingServiceImpl purchasingService;

    @PostMapping("/v1/buy")
    public ResponseEntity<String> buy(@RequestBody BuyItemsDto buyItemsDto) {
        purchasingService.buy(buyItemsDto);
        return ResponseEntity.ok("FUNCIONOU");
    }
}
