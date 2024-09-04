package com.compass.e_commerce.model;

import com.compass.e_commerce.model.enums.StageSale;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_sales")
public class Sale implements Serializable {

    private static final long serialVersionUID= 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime creationTimestamp;

    @Column(nullable = true)
    private LocalDateTime confirmationTimestamp;

    @OneToMany(mappedBy = "id.sale", cascade = CascadeType.ALL)
    private Set<SaleGame> saleGame = new HashSet<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StageSale stageSale;

    private double totalPrice;
}
