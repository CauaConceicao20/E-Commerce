package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.sale.*;
import com.compass.e_commerce.model.Sale;
import com.compass.e_commerce.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
@Tag(name = "Sale")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class SaleController {

    private final SaleService saleService;

    @PostMapping("/v1/create")
    @Operation(summary = "Create User")
    @ApiResponse(responseCode = "201", description = "Criação de sale bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> create(@RequestBody @Valid SaleRegistrationDto saleRegistrationDto) {
        Sale sale = saleService.convertDtoToEntity(saleRegistrationDto);
        saleService.create(sale);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/v1/getAll")
    @Operation(summary = "List Sales")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<List<SaleListDto>> list() {
        var saleList = saleService.getAll();
        return ResponseEntity.ok().body(saleList);
    }

    @PutMapping("/v1/confirm/{id}")
    @Operation(summary = "Confirm Sale")
    @ApiResponse(responseCode = "200", description = "Confirmação de venda bem sucedida")
    @ApiResponse(responseCode = "404", description = "Sale não encontrada")
    @ApiResponse(responseCode = "409", description = "Essa venda ja foi confirmada")
    public ResponseEntity<SaleDetailsDto> confirmSale(@PathVariable Long id) {
        Sale sale = saleService.confirmedSale(id);
        return ResponseEntity.ok().body(new SaleDetailsDto(sale));
    }

    @GetMapping("/v1/reportDay")
    @Operation(summary = "Report Sales Day")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "204", description = "Nenhuma venda encontrada neste dia")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity <List<SaleReportListDto>> generationReportDay(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleService.saleReportsDay(date).stream().map(SaleReportListDto::new).toList();

        if(!saleList.isEmpty()) {
            return ResponseEntity.ok().body(saleList);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1/reportWeek")
    @Operation(summary = "Report Sales Week")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "204", description = "Nenhuma venda encontrada nesta Semana")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<List<SaleReportListDto>> generationReportWeek(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleService.saleReportsWeek(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            return ResponseEntity.ok().body(saleList);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1/reportMonth")
    @Operation(summary = "Report Sales Month")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "204", description = "Nenhuma venda encontrada neste mês")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<List<SaleReportListDto>> generationReportMonth(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleService.saleReportsMonth(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            return ResponseEntity.ok().body(saleList);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/v1/update")
    @Operation(summary = "Update Sale")
    @ApiResponse(responseCode = "200", description = "Atualização bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dado incorretos")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    @ApiResponse(responseCode = "409", description = "A venda ja foi confirmada")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<SaleDetailsDto> updateSale(@RequestBody @Valid SaleUpdateDto saleUpdateDto) {
        var sale = saleService.update(saleUpdateDto);
        return ResponseEntity.ok().body(new SaleDetailsDto(sale));
    }

    @PutMapping("/v1/swap")
    @Operation(summary = "Exchange Games in Sale")
    @ApiResponse(responseCode = "200", description = "Troca bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dado incorretos")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    @ApiResponse(responseCode = "409", description = "A venda ja foi confirmada")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<SaleDetailsDto> swapGame(@RequestBody @Valid SwapGameDto swapGameDto) {
        Sale sale = saleService.swapGame(swapGameDto);
        return ResponseEntity.ok().body(new SaleDetailsDto(sale));
    }

    @DeleteMapping("/v1/delete/{id}")
    @Operation(summary = "Delete Sale")
    @ApiResponse(responseCode = "204", description = "Deleção bem sucedida")
    @ApiResponse(responseCode = "404", description = "Sale não encontrado")
    @ApiResponse(responseCode = "409", description = "Sale já foi confirmada")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        saleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


