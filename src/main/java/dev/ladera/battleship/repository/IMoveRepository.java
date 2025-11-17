package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Move;
import java.sql.SQLException;
import java.util.List;

public interface IMoveRepository {
    Move findById(long id) throws SQLException;

    List<Move> findByGameId(long gameId) throws SQLException;

    Move findLatestMove(long gameId) throws SQLException;

    Move save(Move move) throws SQLException;

    void deleteById(long id) throws SQLException;
}
