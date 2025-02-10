package com.compass.e_commerce.service.interfaces;

public interface OrderService<T, SG, OR>{

    T confirmedOrder(Long id);

    T swapGame(SG swapGameDto);

    T convertDtoToEntity(OR OrderRegistrationDto);
}
