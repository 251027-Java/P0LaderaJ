package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Game;
import java.util.List;

public interface IGameRepository {
    Game findById(long id);

    List<Game> findByPlayerId(long id);

    void save(Game game);

    void deleteById(long id);
}
