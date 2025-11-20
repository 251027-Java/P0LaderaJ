package dev.ladera.battleship.config;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private static final String uri = "jdbc:postgresql://localhost:5432/postgres";
    private static final String user = "postgres";
    private static final String password = "secret";

    private Connection connection;

    public Config() {
        try {
            LOGGER.info("Connecting to PostgreSQL database");
            connection = DriverManager.getConnection(uri, user, password);
            LOGGER.info("Successfully connected to PostgreSQL database");
        } catch (SQLException e) {
            LOGGER.error("Connection failed", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
