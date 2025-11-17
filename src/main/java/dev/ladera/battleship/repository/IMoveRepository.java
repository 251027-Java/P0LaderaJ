package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Move;
import java.util.List;

public interface IMoveRepository {
    Move findById(long id);

    List<Move> findByGameId(long gameId);

    void save(Move move);

    void deleteById(long id);
}
