package com.compass.e_commerce.repository;

import com.compass.e_commerce.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT s FROM Sale s WHERE FUNCTION('DATE', s.confirmationTimestamp) = :date AND s.stageSale = 'CONFIRMED'")
    List<Sale> findByConfirmationDateAndStage(@Param("date") LocalDate date);

    @Query("SELECT s FROM Sale s WHERE FUNCTION('DATE', s.confirmationTimestamp) BETWEEN :startDate AND :endDate AND s.stageSale = 'CONFIRMED'")
    List<Sale> findByConfirmationDateBetweenAndStage(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
