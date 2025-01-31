package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.stock.StockDto;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.service.GameService;
import com.compass.e_commerce.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Tag(name = "Stock")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class StockController {

    private final StockService stockService;
    private final GameService gameService;

    @PutMapping("/v1/reduction/{id}")
    @CacheEvict(value = "games", allEntries = true)
    @Operation(summary = "Reduction of Stock")
    @ApiResponse(responseCode = "204", description = "Estoque reduzido com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados Invalidos")
    @ApiResponse(responseCode = "404", description = "Game não existe")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<Void> reductionStock(@PathVariable Long id, @RequestBody @Valid StockDto stockDto) {
       Game game =  gameService.getById(id);
       stockService.stockReduction(game, stockDto.quantity());


        return ResponseEntity.noContent().build();
    }

    @PutMapping("/v1/replenishment/{id}")
    @CacheEvict(value = "games", allEntries = true)
    @Operation(summary = "Replenishment of Stock")
    @ApiResponse(responseCode = "204", description = "Reposição de estoque bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dados Invalidos")
    @ApiResponse(responseCode = "404", description = "Game não existe")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<Void> repositionStock(@PathVariable Long id, @RequestBody @Valid StockDto stockDto) {
        Game game =  gameService.getById(id);
        stockService.stockReplenishment(game, stockDto.quantity());

        return ResponseEntity.noContent().build();
    }
}
