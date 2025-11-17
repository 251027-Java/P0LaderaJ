package dev.ladera.battleship.service;

import dev.ladera.battleship.repository.IGameRepository;
import dev.ladera.battleship.repository.IMoveRepository;
import dev.ladera.battleship.repository.IPlayerRepository;
import dev.ladera.battleship.repository.IShipRepository;

public class GameService implements IGameService {
    private final IGameRepository gameRepository;
    private final IPlayerRepository playerRepository;
    private final IMoveRepository moveRepository;
    private final IShipRepository shipRepository;

    public GameService(
            IGameRepository gameRepository,
            IPlayerRepository playerRepository,
            IMoveRepository moveRepository,
            IShipRepository shipRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.moveRepository = moveRepository;
        this.shipRepository = shipRepository;
    }
}
