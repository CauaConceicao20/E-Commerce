package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.model.Sale;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface CrudService <T> {

    T create(T entity);

    List<T> getAll();

    T getById(Long id);

}
