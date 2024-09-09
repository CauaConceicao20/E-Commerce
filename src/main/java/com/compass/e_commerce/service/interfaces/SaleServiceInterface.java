package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.dto.sale.SaleListDto;
import com.compass.e_commerce.dto.sale.SaleUpdateDto;
import com.compass.e_commerce.model.Sale;

import java.util.List;

public interface SaleServiceInterface {
    Sale create(Sale sale);
    List<SaleListDto> getAll();
    Sale update(SaleUpdateDto saleUpdateDto);
    void delete(Long id);
}
