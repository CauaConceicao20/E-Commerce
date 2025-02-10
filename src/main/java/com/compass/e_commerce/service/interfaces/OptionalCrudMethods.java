package com.compass.e_commerce.service.interfaces;

public interface OptionalCrudMethods <T, D>{

    T update(Long id, D dto);

    default void delete(Long id) {
        throw new UnsupportedOperationException("Delete não suportado por padrão.");
    }
}
