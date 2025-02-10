package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.game.GameDetailsDto;
import com.compass.e_commerce.dto.game.GameListDto;
import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.service.GameServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/game")
@RequiredArgsConstructor
@Tag(name = "Game")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class GameController {

    private final GameServiceImpl gameServiceImpl;

    @PostMapping("/v1/createRole")
    @Operation(summary = "Create Game")
    @ApiResponse(responseCode = "201", description = "Criação de Game bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<GameDetailsDto> createRequest(@RequestBody @Valid GameRegistrationDto gameDto, UriComponentsBuilder uriBuilder) {
        Game game = gameServiceImpl.convertDtoToEntity(gameDto);
        gameServiceImpl.create(game);
        GameDetailsDto gameDetailsDto = new GameDetailsDto(game);
        gameDetailsDto.add(linkTo(methodOn(GameController.class).getById(game.getId())).withSelfRel());
        gameDetailsDto.add(linkTo(methodOn(GameController.class).update(game.getId(), null)).withRel("update"));
        gameDetailsDto.add(linkTo(methodOn(GameController.class).delete(game.getId())).withRel("delete"));
        gameDetailsDto.add(linkTo(methodOn(GameController.class).listAll()).withRel("allGames"));

        var uri = uriBuilder.path("/game/{id}").buildAndExpand(game.getId()).toUri();

        return ResponseEntity.created(uri).body(gameDetailsDto);
    }

    @GetMapping("/v1/getAll")
    @Operation(summary = "List Games")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<CollectionModel<GameListDto>> listAll() {
        var listGame = gameServiceImpl.getAll().stream().map(GameListDto::new).toList();
        for(GameListDto game : listGame) {
            game.add(linkTo(methodOn(GameController.class).getById(game.getId())).withSelfRel());
            game.add(linkTo(methodOn(GameController.class).update(game.getId(), null)).withRel("update"));
            game.add(linkTo(methodOn(GameController.class).delete(game.getId())).withRel("delete"));
        }
        CollectionModel<GameListDto> collectionModel = CollectionModel.of(listGame);
        collectionModel.add(linkTo(methodOn(GameController.class).createRequest(null, null)).withRel("createRole"));

        return ResponseEntity.ok().body(collectionModel);
    }

    @GetMapping("/v1/getGameId/{id}")
    public ResponseEntity<GameDetailsDto> getById(@PathVariable Long id) {
        var game = gameServiceImpl.getById(id);
        GameDetailsDto gameDetailsDto = new GameDetailsDto(game);
        gameDetailsDto.add(linkTo(methodOn(GameController.class).createRequest(null, null)).withRel("createRole"));
        gameDetailsDto.add(linkTo(methodOn(GameController.class).update(game.getId(), null)).withRel("update"));
        gameDetailsDto.add(linkTo(methodOn(GameController.class).delete(game.getId())).withRel("delete"));
        gameDetailsDto.add(linkTo(methodOn(GameController.class).listAll()).withRel("allGames"));

        return ResponseEntity.ok().body(gameDetailsDto);
    }

    @PutMapping("/v1/update/{id}")
    @Operation(summary = "Update Game")
    @ApiResponse(responseCode = "200", description = "Atualização bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dado incorretos")
    @ApiResponse(responseCode = "404", description = "Game não encontrado")
    @ApiResponse(responseCode = "409", description = "O Game está inativado")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<GameDetailsDto> update(@PathVariable Long id, @RequestBody @Valid GameUpdateDto gameUpdateDto) {
        Game game = gameServiceImpl.update(id, gameUpdateDto);
        GameDetailsDto gameDetailsDto = new GameDetailsDto(game);
        gameDetailsDto.add(linkTo(methodOn(GameController.class).getById(id)).withSelfRel());
        gameDetailsDto.add(linkTo(methodOn(GameController.class).createRequest(null, null)).withRel("createRole"));
        gameDetailsDto.add(linkTo(methodOn(GameController.class).delete(game.getId())).withRel("delete"));
        gameDetailsDto.add(linkTo(methodOn(GameController.class).listAll()).withRel("allGames"));

        return ResponseEntity.ok().body(gameDetailsDto);
    }

    @PutMapping("/v1/isActive/{id}")
    @Transactional
    @CacheEvict(value = "games", allEntries = true)
    @Operation(summary = "Active Game")
    @ApiResponse(responseCode = "204", description = "Ativação bem sucedida")
    @ApiResponse(responseCode = "404", description = "User não encontrado")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> activeGame(@PathVariable Long id) {
        Game game = gameServiceImpl.getById(id);
        game.isActive();

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/v1/isInactive/{id}")
    @Transactional
    @CacheEvict(value = "games", allEntries = true)
    @Operation(summary = "Inactive Game")
    @ApiResponse(responseCode = "204", description = "Inativação bem sucedida")
    @ApiResponse(responseCode = "404", description = "User não encontrado")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> inactiveGame(@PathVariable Long id) {
        Game game = gameServiceImpl.getById(id);
        game.isInactive();

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/delete/{id}")
    @Operation(summary = "Delete Game")
    @ApiResponse(responseCode = "204", description = "Deleção bem sucedida")
    @ApiResponse(responseCode = "404", description = "Game não encontrado")
    @ApiResponse(responseCode = "409", description = "Game está inativado")
    @ApiResponse(responseCode = "409", description = "Game está associado a vendas")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameServiceImpl.delete(id);

        return ResponseEntity.noContent().build();
    }
}
