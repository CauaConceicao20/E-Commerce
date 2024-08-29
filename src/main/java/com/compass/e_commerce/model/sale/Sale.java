package com.compass.e_commerce.model.sale;

import com.compass.e_commerce.dto.sale.SaleRegistrationDto;
import com.compass.e_commerce.model.game.Game;
import com.compass.e_commerce.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = true)
    private LocalDateTime dateTime;

    @ElementCollection
    @CollectionTable(
            name = "tb_sale_games",
            joinColumns = @JoinColumn(name = "sale_id")
    )
    @MapKeyJoinColumn(name = "game_id")
    @Column(name = "quantity")
    private Map<Game, Integer> games = new HashMap<>();

    public Sale(Map<Game, Integer> games) {
        this.games = games;
    }
}
