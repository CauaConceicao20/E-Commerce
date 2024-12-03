package com.compass.e_commerce.model;

import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.model.enums.GenderEnum;
import com.compass.e_commerce.model.enums.PlatformEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.springframework.cache.annotation.CacheEvict;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Entity
@Table(name = "tb_games")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Enumerated(EnumType.STRING)
    private PlatformEnum platform;

    @Column(nullable = false)
    @DecimalMin("1.0")
    private double price;
/*
    @ManyToMany(mappedBy = "games")
    private List<Cart> carts = new ArrayList<>();

 */

    @OneToMany(mappedBy = "id.game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartGameItem> cartGameItem = new ArrayList<>();

    @OneToMany(mappedBy = "id.game", cascade = CascadeType.ALL)
    private Set<SaleGame> saleGame = new HashSet<>();

    private boolean active;

    public Game(GameRegistrationDto dataDto) {
        this.active = true;
        this.name = dataDto.name();
        this.description = dataDto.description();
        this.gender = dataDto.gender();
        this.stock = new Stock(dataDto.stock());
        this.platform = dataDto.platform();
        this.price = dataDto.price();
    }

    public Game(GameUpdateDto dataDto) {
        this.active = true;
        this.name = dataDto.name();
        this.description = dataDto.description();
        this.gender = dataDto.gender();
        this.platform = dataDto.platform();
        this.price = dataDto.price();
    }

    public boolean getActive() {
        return this.active;
    }

    @CacheEvict(value = "games", allEntries = true)
    public void isActive() {
        this.active = true;
    }
    @CacheEvict(value = "games", allEntries = true)
    public void isInactive() {
        this.active = false;
    }
}
