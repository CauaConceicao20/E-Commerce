package com.compass.e_commerce.repository;

import com.compass.e_commerce.model.sale.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT s FROM Sale s WHERE FUNCTION('DATE', s.dateTime) = :date")
    List<Sale> findByDateTime(@Param("date") LocalDate date);

    @Query("SELECT s FROM Sale s WHERE FUNCTION('DATE', s.dateTime) BETWEEN :startDate AND :endDate")
    List<Sale> findBySaleDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
