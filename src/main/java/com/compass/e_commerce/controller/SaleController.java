package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.sale.SaleListDto;
import com.compass.e_commerce.dto.sale.SaleReportListDto;
import com.compass.e_commerce.model.Sale;
import com.compass.e_commerce.service.SaleServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/sale")
@RequiredArgsConstructor
public class SaleController {

    private final SaleServiceImpl saleServiceImpl;

    @GetMapping("v1/listAll")
    public ResponseEntity<List<SaleListDto>> listAll() {
        List<SaleListDto> sales = saleServiceImpl.getAll().stream().map(SaleListDto::new).toList();
        return ResponseEntity.ok().body(sales);
    }

    @GetMapping("v1/getSaleId/{id}")
    public ResponseEntity<SaleListDto> getById(@PathVariable Long id) {
       Sale sale = saleServiceImpl.getById(id);

       return ResponseEntity.ok().body(new SaleListDto(sale));
    }

    @GetMapping("/v1/reportSaleDay")
    @Operation(summary = "Report Sales Day")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "204", description = "Nenhuma venda encontrada neste dia")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<CollectionModel<SaleReportListDto>> generationReportDay(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleServiceImpl.detailedSaleReportsDay(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            for(SaleReportListDto sale : saleList) {
                sale.add(linkTo(methodOn(OrderController.class).getById(sale.getSaleId())).withSelfRel());
            }
            CollectionModel<SaleReportListDto> collectionModel = CollectionModel.of(saleList);
            collectionModel.add(linkTo(methodOn(OrderController.class).create(null,null)).withRel("createRole"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportWeek(date)).withRel("reportWeek"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportMonth(date)).withRel("reportMonth"));
            collectionModel.add(linkTo(methodOn(OrderController.class).list()).withRel("allSales"));
            return ResponseEntity.ok().body(collectionModel);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1/reportSaleWeek")
    @Operation(summary = "Report Sales Week")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "204", description = "Nenhuma venda encontrada nesta Semana")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<CollectionModel<SaleReportListDto>> generationReportWeek(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleServiceImpl.detailedSaleReportsWeek(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            for(SaleReportListDto sale : saleList) {
                sale.add(linkTo(methodOn(OrderController.class).getById(sale.getSaleId())).withSelfRel());
            }
            CollectionModel<SaleReportListDto> collectionModel = CollectionModel.of(saleList);
            collectionModel.add(linkTo(methodOn(OrderController.class).create(null, null)).withRel("createRole"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportDay(date)).withRel("reportDay"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportMonth(date)).withRel("reportMonth"));
            collectionModel.add(linkTo(methodOn(OrderController.class).list()).withRel("allSales"));

            return ResponseEntity.ok().body(collectionModel);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1/reportSaleMonth")
    @Operation(summary = "Report Sales Month")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "204", description = "Nenhuma venda encontrada neste mês")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<CollectionModel<SaleReportListDto>> generationReportMonth(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var saleList  = saleServiceImpl.detailedSaleReportsMonth(date).stream().map(SaleReportListDto::new).toList();
        if(!saleList.isEmpty()) {
            for (SaleReportListDto sale : saleList) {
                sale.add(linkTo(methodOn(OrderController.class).getById(sale.getSaleId())).withSelfRel());
            }
            CollectionModel<SaleReportListDto> collectionModel = CollectionModel.of(saleList);
            collectionModel.add(linkTo(methodOn(OrderController.class).create(null, null)).withRel("createRole"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportDay(date)).withRel("reportDay"));
            collectionModel.add(linkTo(methodOn(SaleController.class).generationReportWeek(date)).withRel("reportWeek"));
            collectionModel.add(linkTo(methodOn(OrderController.class).list()).withRel("allSales"));
            return ResponseEntity.ok().body(collectionModel);
        }
        return ResponseEntity.noContent().build();
    }

}
