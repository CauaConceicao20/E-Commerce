package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.game.GameDetailsDto;
import com.compass.e_commerce.dto.game.GameListDto;
import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.service.GameService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;


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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDetailsDto> update(@RequestBody @Valid GameUpdateDto gameUpdateDto) {
        Game game = gameService.update(gameUpdateDto);

        return ResponseEntity.ok().body(new GameDetailsDto(game));
    }

    @PutMapping("/isActive/{id}")
    @Transactional
    @CacheEvict(value = "games", allEntries = true)
    public ResponseEntity<Void> activeGame(@PathVariable Long id) {
        Game game = gameService.getId(id);
        game.isActive();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/isInactive/{id}")
    @Transactional
    @CacheEvict(value = "games", allEntries = true)
    public ResponseEntity<Void> inactiveGame(@PathVariable Long id) {
        Game game = gameService.getId(id);
        game.isInactive();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
