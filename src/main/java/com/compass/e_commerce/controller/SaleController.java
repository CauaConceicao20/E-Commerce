package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.order.*;
import com.compass.e_commerce.model.Order;
import com.compass.e_commerce.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
@Tag(name = "Order")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class SaleController {

    private final SaleService saleService;

    @PostMapping("/v1/create")
    @Operation(summary = "Create User")
    @ApiResponse(responseCode = "201", description = "Criação de sale bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<SaleDetailsDto> create(@RequestBody @Valid SaleRegistrationDto saleRegistrationDto, UriComponentsBuilder uriBuilder) {
        Order order = saleService.convertDtoToEntity(saleRegistrationDto);
        saleService.create(order);
        SaleDetailsDto saleDetailsDto = new SaleDetailsDto(order);
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).getById(order.getId())).withSelfRel());
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).confirmSale(order.getId())).withRel("confirmSale"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).getById(order.getId())).withRel("update"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).getById(order.getId())).withRel("delete"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).list()).withRel("allSales"));
        var uri = uriBuilder.path("/order/{id}").buildAndExpand(order.getId()).toUri();

        return ResponseEntity.status(HttpStatus.CREATED).body(saleDetailsDto);
    }

    @GetMapping("/v1/getAll")
    @Operation(summary = "List Sales")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<CollectionModel<SaleListDto>> list() {
        var saleList = saleService.getAll();
        for(SaleListDto sale : saleList) {
            sale.add(linkTo(methodOn(SaleController.class).getById(sale.getId())).withSelfRel());
            sale.add(linkTo(methodOn(SaleController.class).getById(sale.getId())).withRel("update"));
            sale.add(linkTo(methodOn(SaleController.class).getById(sale.getId())).withRel("delete"));
        }
        CollectionModel<SaleListDto> collectionModel = CollectionModel.of(saleList);
        collectionModel.add(linkTo(methodOn(SaleController.class).create(null, null)).withRel("create"));

        return ResponseEntity.ok().body(collectionModel);
    }

    @GetMapping("/v1/getById/{id}")
    @Operation(summary = "List Sales")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<SaleDetailsDto> getById(@PathVariable Long id) {
        var sale = saleService.getId(id);
        SaleDetailsDto saleDetailsDto = new SaleDetailsDto(sale);
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).create(null, null)).withRel("create"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).updateSale(sale.getId(), null)).withRel("update"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).delete(sale.getId())).withRel("delete"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).list()).withRel("allSales"));

        return ResponseEntity.ok().body(saleDetailsDto);
    }

    @PutMapping("/v1/confirm/{id}")
    @Operation(summary = "Confirm Order")
    @ApiResponse(responseCode = "200", description = "Confirmação de venda bem sucedida")
    @ApiResponse(responseCode = "404", description = "Order não encontrada")
    @ApiResponse(responseCode = "409", description = "Essa venda ja foi confirmada")
    public ResponseEntity<SaleDetailsDto> confirmSale(@PathVariable Long id) {
        Order order = saleService.confirmedSale(id);
        SaleDetailsDto saleDetailsDto = new SaleDetailsDto(order);
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).getById(order.getId())).withSelfRel());
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).create(null,null)).withRel("create"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).generationReportDay(LocalDate.from(order.getConfirmationTimestamp()))).withRel("reportSaleDay"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).generationReportWeek(LocalDate.from(order.getConfirmationTimestamp()))).withRel("reportSaleWeek"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).generationReportMonth(LocalDate.from(order.getConfirmationTimestamp()))).withRel("reportSalMonth"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).list()).withRel("allSales"));

        return ResponseEntity.ok().body(saleDetailsDto);
    }

    @GetMapping("/v1/reportDay")
    @Operation(summary = "Report Sales Day")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "204", description = "Nenhuma venda encontrada neste dia")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity <CollectionModel<SaleReportListDto>> generationReportDay(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleService.saleReportsDay(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            for(SaleReportListDto sale : saleList) {
                sale.add(linkTo(methodOn(SaleController.class).getById(sale.getSaleId())).withSelfRel());
            }
            CollectionModel<SaleReportListDto> collectionModel = CollectionModel.of(saleList);
            collectionModel.add(linkTo(methodOn(SaleController.class).create(null,null)).withRel("create"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportWeek(date)).withRel("reportWeek"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportMonth(date)).withRel("reportMonth"));
            collectionModel.add(linkTo(methodOn(SaleController.class).list()).withRel("allSales"));
            return ResponseEntity.ok().body(collectionModel);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1/reportWeek")
    @Operation(summary = "Report Sales Week")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "204", description = "Nenhuma venda encontrada nesta Semana")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<CollectionModel<SaleReportListDto>> generationReportWeek(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleService.saleReportsWeek(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            for(SaleReportListDto sale : saleList) {
                sale.add(linkTo(methodOn(SaleController.class).getById(sale.getSaleId())).withSelfRel());
            }
            CollectionModel<SaleReportListDto> collectionModel = CollectionModel.of(saleList);
            collectionModel.add(linkTo(methodOn(SaleController.class).create(null, null)).withRel("create"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportDay(date)).withRel("reportDay"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportMonth(date)).withRel("reportMonth"));
            collectionModel.add(linkTo(methodOn(SaleController.class).list()).withRel("allSales"));

            return ResponseEntity.ok().body(collectionModel);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1/reportMonth")
    @Operation(summary = "Report Sales Month")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "204", description = "Nenhuma venda encontrada neste mês")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<CollectionModel<SaleReportListDto>> generationReportMonth(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleService.saleReportsMonth(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            for (SaleReportListDto sale : saleList) {
                sale.add(linkTo(methodOn(SaleController.class).getById(sale.getSaleId())).withSelfRel());
            }
            CollectionModel<SaleReportListDto> collectionModel = CollectionModel.of(saleList);
            collectionModel.add(linkTo(methodOn(SaleController.class).create(null, null)).withRel("create"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportDay(date)).withRel("reportDay"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportWeek(date)).withRel("reportWeek"));
            collectionModel.add(linkTo(methodOn(SaleController.class).list()).withRel("allSales"));
            return ResponseEntity.ok().body(collectionModel);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/v1/update/{id}")
    @Operation(summary = "Update Order")
    @ApiResponse(responseCode = "200", description = "Atualização bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dado incorretos")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    @ApiResponse(responseCode = "409", description = "A venda ja foi confirmada")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<SaleDetailsDto> updateSale(@PathVariable Long id, @RequestBody @Valid SaleUpdateDto saleUpdateDto) {
        var sale = saleService.update(id, saleUpdateDto);
        SaleDetailsDto saleDetailsDto = new SaleDetailsDto(sale);
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).getById(sale.getId())).withSelfRel());
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).create(null, null)).withRel("create"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).list()).withRel("allSale"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).delete(sale.getId())).withRel("delete"));

        return ResponseEntity.ok().body(saleDetailsDto);
    }

    @PutMapping("/v1/swap")
    @Operation(summary = "Exchange Games in Order")
    @ApiResponse(responseCode = "200", description = "Troca bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dado incorretos")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    @ApiResponse(responseCode = "409", description = "A venda ja foi confirmada")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<SaleDetailsDto> swapGame(@RequestBody @Valid SwapGameDto swapGameDto) {
        Order order = saleService.swapGame(swapGameDto);
        SaleDetailsDto saleDetailsDto = new SaleDetailsDto(order);
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).getById(order.getId())).withSelfRel());
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).create(null, null)).withRel("create"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).list()).withRel("allSale"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).updateSale(order.getId(), null)).withRel("update"));
        saleDetailsDto.add(linkTo(methodOn(SaleController.class).delete(order.getId())).withRel("delete"));

        return ResponseEntity.ok().body(new SaleDetailsDto(order));
    }

    @DeleteMapping("/v1/delete/{id}")
    @Operation(summary = "Delete Order")
    @ApiResponse(responseCode = "204", description = "Deleção bem sucedida")
    @ApiResponse(responseCode = "404", description = "Order não encontrado")
    @ApiResponse(responseCode = "409", description = "Order já foi confirmada")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        saleService.delete(id);

        return ResponseEntity.noContent().build();
    }
}