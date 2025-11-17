package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Ship;
import java.sql.Connection;
import java.util.List;

public class JdbcShipRepository implements IShipRepository {
    private final Connection connection;

    public JdbcShipRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Ship findById(long id) {
        return null;
    }

    @Override
    public List<Ship> findByGameId(long gameId) {
        return List.of();
    }

    @Override
    public void save(Ship move) {}

    @Override
    public void deleteById(long id) {}
}
