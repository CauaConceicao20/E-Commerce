package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.dto.order.SaleListDto;
import com.compass.e_commerce.dto.order.SaleUpdateDto;
import com.compass.e_commerce.model.Order;

import java.util.List;

public interface SaleServiceImp {
    Order create(Order order);
    List<SaleListDto> getAll();
    Order update(Long id, SaleUpdateDto saleUpdateDto);
    void delete(Long id);
}
