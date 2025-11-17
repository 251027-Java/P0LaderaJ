package dev.ladera.battleship;

import dev.ladera.battleship.config.Config;
import dev.ladera.battleship.repository.*;
import dev.ladera.battleship.service.BattleshipService;
import dev.ladera.battleship.service.GameService;
import dev.ladera.battleship.service.IBattleshipService;
import dev.ladera.battleship.service.IGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    static void main() {
        LOGGER.info("Application started");

        Config config = new Config();

        IShipRepository shipRepository = new JdbcShipRepository(config.getConnection());
        IMoveRepository moveRepository = new JdbcMoveRepository(config.getConnection());
        IPlayerRepository playerRepository = new JdbcPlayerRepository(config.getConnection());
        IGameRepository gameRepository = new JdbcGameRepository(config.getConnection());

        IGameService gameService = new GameService(gameRepository, playerRepository, moveRepository, shipRepository);

        IBattleshipService battleshipService = new BattleshipService(gameService);
        battleshipService.run();

        LOGGER.info("Application closing");
    }
}
