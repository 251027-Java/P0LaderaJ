package dev.ladera.battleship;

import dev.ladera.battleship.repository.IGameRepository;
import dev.ladera.battleship.repository.JdbcGameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    static void main() {
        LOGGER.info("Application started");

        IGameRepository repository = new JdbcGameRepository();
        System.out.println("hello");

        LOGGER.info("Application ending");
    }
}
