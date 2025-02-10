package com.compass.e_commerce.service.interfaces;

public interface GameService<T,D> {
  T convertDtoToEntity(D dto);
}
