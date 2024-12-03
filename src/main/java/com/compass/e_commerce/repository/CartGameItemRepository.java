package com.compass.e_commerce.repository;

import com.compass.e_commerce.model.CartGameItem;
import com.compass.e_commerce.model.pk.CartGameItemPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartGameItemRepository extends JpaRepository<CartGameItem, CartGameItemPK> {
}
