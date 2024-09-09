package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.game.GameDetailsDto;
import com.compass.e_commerce.dto.game.GameListDto;
import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Tag(name = "Game")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class GameController {

    private final GameService gameService;

    @PostMapping("/create")
    @Operation(summary = "Create Game")
    @ApiResponse(responseCode = "201", description = "Criação de Game bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<GameDetailsDto> createRequest(@RequestBody @Valid GameRegistrationDto gameDto, UriComponentsBuilder uriBuilder) {
        Game game = gameService.convertDtoToEntity(gameDto);
        gameService.create(game);

        var uri = uriBuilder.path("/game/{id}").buildAndExpand(game.getId()).toUri();
        return ResponseEntity.created(uri).body(new GameDetailsDto(game));
    }

    @GetMapping("/getAll")
    @Operation(summary = "List Games")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<List<GameListDto>> list() {
        var listGame = gameService.getAll().stream().map(GameListDto::new).toList();
        return ResponseEntity.ok().body(listGame);
    }


    @PutMapping("/update")
    @Operation(summary = "Update Game")
    @ApiResponse(responseCode = "200", description = "Atualização bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dado incorretos")
    @ApiResponse(responseCode = "404", description = "Game não encontrado")
    @ApiResponse(responseCode = "409", description = "O Game está inativado")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<GameDetailsDto> update(@RequestBody @Valid GameUpdateDto gameUpdateDto) {
        Game game = gameService.update(gameUpdateDto);

        return ResponseEntity.ok().body(new GameDetailsDto(game));
    }

    @PutMapping("/isActive/{id}")
    @Transactional
    @CacheEvict(value = "games", allEntries = true)
    @Operation(summary = "Active Game")
    @ApiResponse(responseCode = "204", description = "Ativação bem sucedida")
    @ApiResponse(responseCode = "404", description = "User não encontrado")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> activeGame(@PathVariable Long id) {
        Game game = gameService.getById(id);
        game.isActive();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/isInactive/{id}")
    @Transactional
    @CacheEvict(value = "games", allEntries = true)
    @Operation(summary = "Inactive Game")
    @ApiResponse(responseCode = "204", description = "Inativação bem sucedida")
    @ApiResponse(responseCode = "404", description = "User não encontrado")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> inactiveGame(@PathVariable Long id) {
        Game game = gameService.getById(id);
        game.isInactive();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete Game")
    @ApiResponse(responseCode = "204", description = "Deleção bem sucedida")
    @ApiResponse(responseCode = "404", description = "Game não encontrado")
    @ApiResponse(responseCode = "409", description = "Game está inativado")
    @ApiResponse(responseCode = "409", description = "Game está associado a vendas")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
