package com.compass.e_commerce.model;

import com.compass.e_commerce.model.enums.PaymentMethod;
import com.compass.e_commerce.model.enums.Stage;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_orders")
public class Order implements Serializable {

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

    @OneToMany(mappedBy = "id.order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderGames> orderGames = new HashSet<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Stage stageOrder;

    @Column(nullable = true)
    private String qrCodeId;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private double totalPrice;
}
