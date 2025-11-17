package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Game;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcGameRepository implements IGameRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcGameRepository.class);
    private static final String uri = "jdbc:postgresql://localhost:5432/postgres";
    private static final String user = "postgres";
    private static final String password = "secret";

    private Connection connection;

    public JdbcGameRepository() {
        try {
            LOGGER.info("Connecting to PostgreSQL database");
            connection = DriverManager.getConnection(uri, user, password);
            LOGGER.info("Successfully connected to PostgreSQL database");
        } catch (SQLException e) {
            LOGGER.error(e.toString());
        }
    }

    @Override
    public Game findById(long id) {
        return null;
    }

    @Override
    public void save(Game game) {}

    @Override
    public void delete(Game game) {}
}
