package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.dto.order.OrderListDto;
import com.compass.e_commerce.dto.order.OrderUpdateDto;
import com.compass.e_commerce.model.Order;

import java.util.List;

public interface SaleServiceImp {
    Order create(Order order);
    List<OrderListDto> getAll();
    Order update(Long id, OrderUpdateDto saleUpdateDto);
    void delete(Long id);
}
