package com.compass.e_commerce.controller;

import com.compass.e_commerce.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @PostMapping("/clear")
    public ResponseEntity<Void> clear(@RequestParam("cacheName") String cacheName) {
        cacheService.evictAllCacheValues(cacheName);
        return ResponseEntity.ok().build();
    }
}