package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Game;

public interface IGameRepository {
    Game findById(long id);

    void save(Game game);

    void delete(Game game);
}
