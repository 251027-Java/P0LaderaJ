package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Player;
import java.sql.Connection;

public class JdbcPlayerRepository implements IPlayerRepository {
    private final Connection connection;

    public JdbcPlayerRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Player findById(long id) {
        return null;
    }

    @Override
    public Player findByUsername(String username) {
        return null;
    }

    @Override
    public void save(Player move) {}

    @Override
    public void deleteById(long id) {}
}
