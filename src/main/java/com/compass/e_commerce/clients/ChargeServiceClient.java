package com.compass.e_commerce.clients;

import com.compass.e_commerce.dto.buy.BuyDetailsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name= "payment-service", url = "http://localhost:8090/api/pix")
public interface ChargeServiceClient {

    @PostMapping("v1/pixCreateCharge")
    String pixCreateCharge(@RequestBody BuyDetailsDto buyDetailsDto);

    @GetMapping("/v1/pixCharge")
    String pixDetailsCharge(@RequestParam("txId") String txId);
}

