package dev.ladera.battleship;

import dev.ladera.battleship.repository.IGameRepository;
import dev.ladera.battleship.repository.JdbcGameRepository;
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

        IGameRepository repository = new JdbcGameRepository();
        IGameService gameService = new GameService(repository);

        IBattleshipService battleshipService = new BattleshipService(gameService);
        battleshipService.run();

        LOGGER.info("Application closing");
    }
}
