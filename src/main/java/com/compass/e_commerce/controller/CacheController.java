package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.service.CacheServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
@Tag(name = "Cache")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class CacheController {

    private final CacheServiceImpl cacheServiceImpl;

    @PostMapping("/v1/clear")
    @Operation(summary = "Clear Cache")
    @ApiResponse(responseCode = "204", description = "Cache excluido com sucesso")
    public ResponseEntity<Void> clear(@RequestParam("cacheName") String cacheName) {
        cacheServiceImpl.evictAllCacheValues(cacheName);
        return ResponseEntity.noContent().build();
    }
}