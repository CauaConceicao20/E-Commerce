package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.buy.BuyItemsDto;
import com.compass.e_commerce.dto.order.*;
import com.compass.e_commerce.model.Order;
import com.compass.e_commerce.service.OrderService;
import com.compass.e_commerce.service.PurchasingService;
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

    private final OrderService orderService;
    private final PurchasingService purchasingService;


    @PostMapping("/v1/createRole")
    @Operation(summary = "Create User")
    @ApiResponse(responseCode = "201", description = "Criação de sale bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<OrderDetailsDto> create(@RequestBody @Valid OrderRegistrationDto orderRegistrationDto, UriComponentsBuilder uriBuilder) {
        Order order = orderService.convertDtoToEntity(orderRegistrationDto);
        orderService.create(order);
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
        var saleList = orderService.getAll();
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
        var sale = orderService.getId(id);
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto(sale);
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).create(null, null)).withRel("createRole"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).updateSale(sale.getId(), null)).withRel("update"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).delete(sale.getId())).withRel("delete"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).list()).withRel("allSales"));

        return ResponseEntity.ok().body(orderDetailsDto);
    }

    @PutMapping("/v1/confirm/{id}")
    @Operation(summary = "Confirm Order")
    @ApiResponse(responseCode = "200", description = "Confirmação de venda bem sucedida")
    @ApiResponse(responseCode = "404", description = "Order não encontrada")
    @ApiResponse(responseCode = "409", description = "Essa venda ja foi confirmada")
    public ResponseEntity<OrderDetailsDto> confirmSale(@PathVariable Long id) {
        Order order = orderService.confirmedOrder(id);
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


    @PutMapping("/v1/update/{id}")
    @Operation(summary = "Update Order")
    @ApiResponse(responseCode = "200", description = "Atualização bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dado incorretos")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    @ApiResponse(responseCode = "409", description = "A venda ja foi confirmada")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<OrderDetailsDto> updateSale(@PathVariable Long id, @RequestBody @Valid OrderUpdateDto orderUpdateDto) {
        var sale = orderService.update(id, orderUpdateDto);
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto(sale);
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).getById(sale.getId())).withSelfRel());
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).create(null, null)).withRel("createRole"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).list()).withRel("allSale"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).delete(sale.getId())).withRel("delete"));

        return ResponseEntity.ok().body(orderDetailsDto);
    }

    @PutMapping("/v1/swap")
    @Operation(summary = "Exchange Games in Order")
    @ApiResponse(responseCode = "200", description = "Troca bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dado incorretos")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    @ApiResponse(responseCode = "409", description = "A venda ja foi confirmada")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<OrderDetailsDto> swapGame(@RequestBody @Valid SwapGameDto swapGameDto) {
        Order order = orderService.swapGame(swapGameDto);
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto(order);
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).getById(order.getId())).withSelfRel());
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).create(null, null)).withRel("createRole"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).list()).withRel("allSale"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).updateSale(order.getId(), null)).withRel("update"));
        orderDetailsDto.add(linkTo(methodOn(OrderController.class).delete(order.getId())).withRel("delete"));

        return ResponseEntity.ok().body(new OrderDetailsDto(order));
    }

    @DeleteMapping("/v1/delete/{id}")
    @Operation(summary = "Delete Order")
    @ApiResponse(responseCode = "204", description = "Deleção bem sucedida")
    @ApiResponse(responseCode = "404", description = "Order não encontrado")
    @ApiResponse(responseCode = "409", description = "Order já foi confirmada")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);

        return ResponseEntity.noContent().build();
    }
}