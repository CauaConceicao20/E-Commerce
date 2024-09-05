package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.exception.DeletionNotAllowedException;
import com.compass.e_commerce.exception.GameIsInactiveExcpetion;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.repository.GameRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

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

    public Game getId(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() ->  new EntityNotFoundException("Game não encontrado id: " + id));

        return game;
    }

    public Game update(GameUpdateDto gameUpdateDto) {
        Game game = gameRepository.findById(gameUpdateDto.id())
                .orElseThrow(() -> new NoSuchElementException("Game não encontrado id: " + gameUpdateDto.id()));
        if(game.getActive()) {
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
        }else {
            throw new GameIsInactiveExcpetion("Game está inativado");
        }
        return game;
    }

    @CacheEvict(value = "games", allEntries = true)
    public void delete(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Game não encontrado com o id: " + id));

        if(!game.getActive()) {
            throw new DeletionNotAllowedException("O Game está inativado");
        }

        if(!game.getSaleGame().isEmpty()) {
          throw new DeletionNotAllowedException("O Game está associado a uma Sale.");
        }
        gameRepository.deleteById(id);
    }
}
