package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Player;
import java.sql.SQLException;

public interface IPlayerRepository {
    Player findById(long id) throws SQLException;

    Player findByUsername(String username) throws SQLException;

    Player save(Player player) throws SQLException;

    void deleteById(long id) throws SQLException;
}
