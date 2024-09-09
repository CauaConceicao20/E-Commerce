package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.exception.personalized.DeletionNotAllowedException;
import com.compass.e_commerce.exception.personalized.GameIsInactiveException;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.repository.GameRepository;
import com.compass.e_commerce.service.interfaces.GameServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class GameService implements GameServiceInterface {

    private final GameRepository gameRepository;

    @Transactional
    @CacheEvict(value = "games", allEntries = true)
    public Game create(Game game) {
        return gameRepository.save(game);
    }

    public Game convertDtoToEntity(GameRegistrationDto dataDto) {
        return new Game(dataDto);
    }

    @Cacheable("games")
    public List<Game> getAll() {
        return gameRepository.findByActiveTrue();
    }

    public Game getById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Game não encontrado id: " + id));
        return game;
    }

    @CacheEvict(value = "games", allEntries = true)
    public Game update(GameUpdateDto gameUpdateDto) {
        Game game = gameRepository.findById(gameUpdateDto.id())
                .orElseThrow(() -> new NoSuchElementException("Game não encontrado id: " + gameUpdateDto.id()));
        if (!game.getActive()) {
            throw new GameIsInactiveException("Game está inativado");

        }
        if (gameUpdateDto.name() != null) {
            game.setName(gameUpdateDto.name());
        }
        if (gameUpdateDto.description() != null) {
            game.setDescription(gameUpdateDto.description());
        }
        if (gameUpdateDto.gender() != null) {
            game.setGender(gameUpdateDto.gender());
        }
        if (gameUpdateDto.platform() != null) {
            game.setPlatform(gameUpdateDto.platform());
        }
        if (gameUpdateDto.price() > 0) {
            game.setPrice(gameUpdateDto.price());
        }
        gameRepository.save(game);

        return game;
}

    @CacheEvict(value = "games", allEntries = true)
    public void delete(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Game não encontrado com o id: " + id));

        if (!game.getActive()) {
            throw new GameIsInactiveException("O Game está inativado");
        }

        if (!game.getSaleGame().isEmpty()) {
            throw new DeletionNotAllowedException("O Game está associado a uma Sale.");
        }
        gameRepository.deleteById(id);
    }
}
