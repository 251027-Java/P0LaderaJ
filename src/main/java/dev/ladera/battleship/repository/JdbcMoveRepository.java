package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Move;
import java.sql.Connection;
import java.util.List;

public class JdbcMoveRepository implements IMoveRepository {
    private final Connection connection;

    public JdbcMoveRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Move findById(long id) {
        return null;
    }

    @Override
    public List<Move> findByGameId(long gameId) {
        return List.of();
    }

    @Override
    public void save(Move move) {}

    @Override
    public void deleteById(long id) {}
}
