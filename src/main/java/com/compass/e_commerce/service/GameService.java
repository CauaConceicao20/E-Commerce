package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.game.GameRegistrationDto;
import com.compass.e_commerce.dto.game.GameUpdateDto;
import com.compass.e_commerce.model.game.Game;
import com.compass.e_commerce.model.stock.Stock;
import com.compass.e_commerce.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Game create(Game game) {
        //stockService.create(stock);
        return gameRepository.save(game);
    }

    public Game convertDtoToEntity(GameRegistrationDto dataDto) {
        return new Game(dataDto);
    }

    public List<Game> list() {
        return gameRepository.findByActiveTrue();
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

    public Optional<Game> getId(Long id) {
        return gameRepository.findById(id);
    }

    public void delete(Long id) {
        gameRepository.deleteById(id);
    }
}
