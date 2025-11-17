package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Ship;
import java.util.List;

public interface IShipRepository {
    Ship findById(long id);

    List<Ship> findByGameId(long gameId);

    void save(Ship ship);

    void deleteById(long id);
}
