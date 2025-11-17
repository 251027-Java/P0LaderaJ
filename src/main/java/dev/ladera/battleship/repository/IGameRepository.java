package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Game;
import java.sql.SQLException;
import java.util.List;

public interface IGameRepository {
    Game findById(long id) throws SQLException;

    List<Game> findByPlayerId(long id) throws SQLException;

    Game save(Game game) throws SQLException;

    void deleteById(long id) throws SQLException;
}
