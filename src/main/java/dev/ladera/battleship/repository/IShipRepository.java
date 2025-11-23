package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Ship;

import java.sql.SQLException;
import java.util.List;

public interface IShipRepository {
    Ship findById(long id) throws SQLException;

    List<Ship> findByGameId(long gameId) throws SQLException;

    Ship save(Ship ship) throws SQLException;

    void deleteById(long id) throws SQLException;
}
