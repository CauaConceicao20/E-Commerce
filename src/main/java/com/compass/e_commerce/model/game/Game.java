package com.compass.e_commerce.model.game;

import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
    @Min(1)
    private int quantity;

    @Enumerated(EnumType.STRING)
    private PlatformEnum platform;

    @Column(nullable = false)
    @DecimalMin("1.0")
    private double price;

    private boolean active;

    public Game(GameRegistrationDto dataDto) {
        this.active = true;
        this.name = dataDto.name();
        this.description = dataDto.description();
        this.gender = dataDto.gender();
        this.quantity = dataDto.quantity();
        this.platform = dataDto.platform();
        this.price = dataDto.price();
    }

    public Game(GameUpdateDto dataDto) {
        this.active = true;
        this.name = dataDto.name();
        this.description = dataDto.description();
        this.gender = dataDto.gender();
        this.quantity = dataDto.quantity();
        this.platform = dataDto.platform();
        this.price = dataDto.price();
    }
}
