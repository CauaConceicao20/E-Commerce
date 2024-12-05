package com.compass.e_commerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_cart")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantityOfItems;

    private double totalPrice;

    @OneToMany(mappedBy = "id.cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartGameItem> cartGameItem = new ArrayList<>();

    @OneToOne(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;

    public Cart() {
        this.quantityOfItems = 0;
        this.totalPrice = 0;
    }
}
