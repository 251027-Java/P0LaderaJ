package dev.ladera.battleship;

import dev.ladera.battleship.config.Config;
import dev.ladera.battleship.model.Game;
import dev.ladera.battleship.model.Move;
import dev.ladera.battleship.model.Player;
import dev.ladera.battleship.model.Ship;
import dev.ladera.battleship.repository.*;
import dev.ladera.battleship.screen.IBattleshipScreen;
import dev.ladera.battleship.screen.JlineBattleshipScreen;
import dev.ladera.battleship.service.GameService;
import dev.ladera.battleship.service.IGameService;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    static void main() throws IOException {
        LOGGER.info("Application started");

        try (Config config = new Config()) {
            if (config.getConnection() == null) {
                var str = new AttributedStringBuilder()
                        .style(AttributedStyle.DEFAULT.background(AttributedStyle.RED))
                        .append(" ERROR ")
                        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                        .append(" Cannot connect to database");
                System.out.println(str.toAnsi());
                System.exit(-1);
            }

            IShipRepository shipRepository = new JdbcShipRepository(config.getConnection());
            IMoveRepository moveRepository = new JdbcMoveRepository(config.getConnection());
            IPlayerRepository playerRepository = new JdbcPlayerRepository(config.getConnection());
            IGameRepository gameRepository = new JdbcGameRepository(config.getConnection());

            // playerRepoTest(playerRepository);
            // gameRepoTest(gameRepository);
            // shipRepoTest(shipRepository);
            // moveRepoTest(moveRepository);

            IGameService gameService =
                    new GameService(gameRepository, playerRepository, moveRepository, shipRepository);

            IBattleshipScreen battleshipScreen = new JlineBattleshipScreen(gameService);
            battleshipScreen.run();
        }

        LOGGER.info("Application closing");
    }

    static void playerRepoTest(IPlayerRepository repository) throws SQLException {
        Player p1 = new Player("minidomo", "oisjdfoijsdo", null);
        Player p2 = new Player("cooldude", "oisjdfoijsdo", null);

        repository.save(p1);
        repository.save(p2);

        System.out.println(p1.getId() + " | " + p2.getId());

        System.out.println(repository.findById(p1.getId()));
        System.out.println(repository.findRealByUsername("COolDUDE"));

        repository.deleteById(p2.getId());

        System.out.println(repository.findRealByUsername("COolDUDE"));
    }

    static void gameRepoTest(IGameRepository repository) throws SQLException {
        Game g1 = new Game(10, 10);
        Game g2 = new Game(10, 10);

        repository.save(g1);
        repository.save(g2);

        System.out.println(g1.getId() + " | " + g2.getId());

        System.out.println(repository.findById(g1.getId()));

        repository.deleteById(g2.getId());

        System.out.println(repository.findById(g2.getId()));
    }

    static void shipRepoTest(IShipRepository repository) throws SQLException {
        Ship e1 = new Ship(1, 3, 2, 2, 1L, 1L);
        Ship e2 = new Ship(2, 5, 2, 2, 1L, 1L);

        repository.save(e1);
        repository.save(e2);

        System.out.println(repository.findById(e1.getId()));
        System.out.println(repository.findByGameId(e1.getGameId()));

        repository.deleteById(e2.getId());

        System.out.println(repository.findById(e2.getId()));
    }

    static void moveRepoTest(IMoveRepository repository) throws SQLException {
        Move e1 = new Move(1, 1, 2, 1L, 1L);
        Move e2 = new Move(2, 5, 4, 1L, 1L);

        repository.save(e1);
        repository.save(e2);

        System.out.println(e1.getId() + " | " + e2.getId());

        System.out.println(repository.findById(e1.getId()));
        System.out.println(repository.findByGameId(e2.getGameId()));

        repository.deleteById(e2.getId());

        System.out.println(repository.findById(e2.getId()));
    }
}
