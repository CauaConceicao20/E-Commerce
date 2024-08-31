package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.repository.GameRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Transactional
    @CacheEvict(value = "games", allEntries = true)
    public Game create(Game game) {
        return gameRepository.save(game);
    }

    public Game convertDtoToEntity(GameRegistrationDto dataDto) {
        return new Game(dataDto);
    }

    @Cacheable("games")
    public List<Game> list() {
        return gameRepository.findByActiveTrue();
    }

    public Optional<Game> getId(Long id) {
        return gameRepository.findById(id);
    }

    public Game update(GameUpdateDto gameUpdateDto) {
        Optional<Game> gameOp = getId(gameUpdateDto.id());
        Game game = gameOp.get();

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
        gameRepository.deleteById(id);
    }
}
