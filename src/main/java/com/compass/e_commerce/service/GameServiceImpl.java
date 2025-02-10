package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.exception.personalized.DeletionNotAllowedException;
import com.compass.e_commerce.exception.personalized.GameIsInactiveException;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.repository.GameRepository;
import com.compass.e_commerce.service.interfaces.CrudService;
import com.compass.e_commerce.service.interfaces.GameService;
import com.compass.e_commerce.service.interfaces.OptionalCrudMethods;
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
public class GameServiceImpl implements CrudService<Game>, OptionalCrudMethods<Game, GameUpdateDto>, GameService<Game, GameRegistrationDto> {

    private final GameRepository gameRepository;

    @Override
    @Transactional
    @CacheEvict(value = "games", allEntries = true)
    public Game create(Game game) {
        return gameRepository.save(game);
    }

    @Override
    public Game convertDtoToEntity(GameRegistrationDto dataDto) {
        return new Game(dataDto);
    }

    @Override
    @Cacheable("games")
    public List<Game> getAll() {
        return gameRepository.findByActiveTrue();
    }

    public List<Game> findAllById(List<Long> ids) {
       return gameRepository.findAllById(ids);
    }

    @Override
    @Cacheable("games")
    public Game getById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Game não encontrado id: " + id));
    }

    @Override
    @CacheEvict(value = "games", allEntries = true)
    public Game update(Long id, GameUpdateDto gameUpdateDto) {
        Game game = getById(id);
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

        return gameRepository.save(game);
}

    @Override
    @CacheEvict(value = "games", allEntries = true)
    public void delete(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Game não encontrado com o id: " + id));

        if (!game.getActive()) {
            throw new GameIsInactiveException("O Game está inativado");
        }

        if (!game.getOrderGames().isEmpty()) {
            throw new DeletionNotAllowedException("O Game está associado a uma Order.");
        }
        gameRepository.deleteById(id);
    }
}
