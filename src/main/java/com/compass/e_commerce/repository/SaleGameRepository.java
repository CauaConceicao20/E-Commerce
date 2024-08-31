package com.compass.e_commerce.repository;

import com.compass.e_commerce.model.SaleGame;
import com.compass.e_commerce.model.pk.SaleGamePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleGameRepository extends JpaRepository<SaleGame, SaleGamePK> {

}
