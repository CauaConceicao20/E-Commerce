package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.game.GameDetailsDto;
import com.compass.e_commerce.dto.game.GameListDto;
import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.model.game.Game;
import com.compass.e_commerce.service.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<GameDetailsDto> createRequest(@RequestBody @Valid GameRegistrationDto gameDto, UriComponentsBuilder uriBuilder) {
        Game game = gameService.convertDtoToEntity(gameDto);

        gameService.create(game);

        var uri = uriBuilder.path("/game/{id}").buildAndExpand(game.getId()).toUri();

        return ResponseEntity.created(uri).body(new GameDetailsDto(game));
    }

    @GetMapping("/list")
    public ResponseEntity<List<GameListDto>> list() {
        var listGame = gameService.list().stream().map(GameListDto::new).toList();
        return ResponseEntity.ok().body(listGame);
    }

    @PutMapping("/update")
    public ResponseEntity<GameDetailsDto> update(@RequestBody @Valid GameUpdateDto gameUpdateDto) {
        Game game = gameService.update(gameUpdateDto);

        return ResponseEntity.ok().body(new GameDetailsDto(game));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
