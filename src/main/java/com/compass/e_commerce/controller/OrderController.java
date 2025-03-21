package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.order.*;
import com.compass.e_commerce.model.Order;
import com.compass.e_commerce.service.OrderServiceImpl;
import com.compass.e_commerce.service.PurchasingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
@Tag(name = "Order")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;


    @PostMapping("/v1/createRole")
    @Operation(summary = "Create User")
    @ApiResponse(responseCode = "201", description = "Criação de sale bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<OrderDetailsDto> create(@RequestBody @Valid OrderRegistrationDto orderRegistrationDto, UriComponentsBuilder uriBuilder) {
        Order order = orderServiceImpl.convertDtoToEntity(orderRegistrationDto);
        orderServiceImpl.create(order);
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto(order);
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).getById(order.getId())).withSelfRel());
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).confirmSale(order.getId())).withRel("confirmSale"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).getById(order.getId())).withRel("update"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).getById(order.getId())).withRel("delete"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).list()).withRel("allSales"));
        var uri = uriBuilder.path("/order/{id}").buildAndExpand(order.getId()).toUri();

        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailsDto);
    }

    @GetMapping("/v1/getAll")
    @Operation(summary = "List Sales")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<CollectionModel<OrderListDto>> list() {
        var saleList = orderServiceImpl.getAll().stream().map(OrderListDto::new).toList();
        for(OrderListDto sale : saleList) {
            sale.add(linkTo(methodOn(OrderController.class).getById(sale.getId())).withSelfRel());
            sale.add(linkTo(methodOn(OrderController.class).getById(sale.getId())).withRel("update"));
            sale.add(linkTo(methodOn(OrderController.class).getById(sale.getId())).withRel("delete"));
        }
        CollectionModel<OrderListDto> collectionModel = CollectionModel.of(saleList);
        collectionModel.add(linkTo(methodOn(OrderController.class).create(null, null)).withRel("createRole"));

        return ResponseEntity.ok().body(collectionModel);
    }

    @GetMapping("/v1/getById/{id}")
    @Operation(summary = "List Sales")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<OrderDetailsDto> getById(@PathVariable Long id) {
        var sale = orderServiceImpl.getById(id);
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto(sale);
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).create(null, null)).withRel("createRole"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).list()).withRel("allSales"));

        return ResponseEntity.ok().body(orderDetailsDto);
    }

    @PutMapping("/v1/confirm/{id}")
    @Operation(summary = "Confirm Order")
    @ApiResponse(responseCode = "200", description = "Confirmação de venda bem sucedida")
    @ApiResponse(responseCode = "404", description = "Order não encontrada")
    @ApiResponse(responseCode = "409", description = "Essa venda ja foi confirmada")
    public ResponseEntity<OrderDetailsDto> confirmSale(@PathVariable Long id) {
        Order order = orderServiceImpl.confirmedOrder(id);
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto(order);
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).getById(order.getId())).withSelfRel());
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).create(null,null)).withRel("createRole"));
        /*orderDetailsDto.add(linkTo(methodOn(OrderController.class).generationReportDay(LocalDate.from(order.getConfirmationTimestamp()))).withRel("reportSaleDay"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).generationReportWeek(LocalDate.from(order.getConfirmationTimestamp()))).withRel("reportSaleWeek"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).generationReportMonth(LocalDate.from(order.getConfirmationTimestamp()))).withRel("reportSalMonth"));

         */
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).list()).withRel("allSales"));

        return ResponseEntity.ok().body(orderDetailsDto);
    }
}