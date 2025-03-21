package com.compass.e_commerce.service.interfaces;

public interface OrderService<T, OR>{

    T confirmedOrder(Long id);

    T convertDtoToEntity(OR OrderRegistrationDto);
}
