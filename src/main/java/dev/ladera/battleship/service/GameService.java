package dev.ladera.battleship.service;

import dev.ladera.battleship.repository.IGameRepository;

public class GameService implements IGameService {
    private final IGameRepository gameRepository;

    public GameService(IGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
}
