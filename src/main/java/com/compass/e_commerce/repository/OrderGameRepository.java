package com.compass.e_commerce.repository;

import com.compass.e_commerce.model.OrderGames;
import com.compass.e_commerce.model.pk.OrderGamePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderGameRepository extends JpaRepository<OrderGames, OrderGamePK> {

}
