package com.compass.e_commerce.model.game;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "tb_games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    private PlatformEnum platform;

    @Column(nullable = false)
    private double price;
}
