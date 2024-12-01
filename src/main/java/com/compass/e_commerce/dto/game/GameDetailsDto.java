package com.compass.e_commerce.dto.game;

import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.enums.GenderEnum;
import com.compass.e_commerce.model.enums.PlatformEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameDetailsDto extends RepresentationModel<GameDetailsDto> {

    private Long id;

    private String name;

    private String description;

    private GenderEnum gender;

    private int stock;

    private PlatformEnum platform;

    private Double price;

    public GameDetailsDto(Game game) {
        this(game.getId(),
            game.getName(),
            game.getDescription(),
            game.getGender(),
            game.getStock().getQuantity(),
            game.getPlatform(),
            game.getPrice()
        );
    }
}
