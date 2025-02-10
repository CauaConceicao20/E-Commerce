package com.compass.e_commerce.service.interfaces;

import java.time.LocalDate;
import java.util.List;

public interface SaleService<T> {
    List<T> saleReportsDay(LocalDate localDate);

    List<T> saleReportsWeek(LocalDate localDate);

    List<T> saleReportsMonth(LocalDate localDate);

}
