package com.compass.e_commerce.service.interfaces;

import java.time.LocalDate;
import java.util.List;

public interface SaleService<T> {

    List<T> detailedSaleReportsDay(LocalDate localDate);

    List<T> detailedSaleReportsWeek(LocalDate localDate);

    List<T> detailedSaleReportsMonth(LocalDate localDate);

}
