package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.model.Role;

import java.util.List;

public interface RoleService <T, D> {
    T create(T entity);

    List<T> getAll();

    T convertDtoToEntity(D dto);
}
