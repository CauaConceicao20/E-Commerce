package com.compass.e_commerce.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

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
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = true)
    private LocalDateTime dateTime;

    @OneToMany(mappedBy = "id.sale", cascade = CascadeType.ALL)
    private Set<SaleGame> salegame = new HashSet<>();
/*

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

    public Map<String, Map<Integer, Double>> returnGameName() {
        Map<String, Map<Integer, Double>> mapNameGame = new HashMap<>();

        for (Map.Entry<Game, Integer> mapGames : getGames().entrySet()) {
            Game game = mapGames.getKey();
            Integer quantity = mapGames.getValue();

            if (game != null) {
                Map<Integer, Double> quantityPriceMap = mapNameGame.get(game.getName());
                if (quantityPriceMap == null) {
                    quantityPriceMap = new HashMap<>();
                    mapNameGame.put(game.getName(), quantityPriceMap);
                }
                quantityPriceMap.put(quantity, game.getPrice() * quantity);
            }
        }
        return mapNameGame;
    }

 */
}
