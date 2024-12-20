package com.compass.e_commerce.repository;

import com.compass.e_commerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Order, Long> {
    @Query("SELECT s FROM Order s WHERE FUNCTION('DATE', s.confirmationTimestamp) = :date AND s.stageOrder = 'CONFIRMED'")
    List<Order> findByConfirmationDateAndStage(@Param("date") LocalDate date);

    @Query("SELECT s FROM Order s WHERE FUNCTION('DATE', s.confirmationTimestamp) BETWEEN :startDate AND :endDate AND s.stageOrder = 'CONFIRMED'")
    List<Order> findByConfirmationDateBetweenAndStage(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
