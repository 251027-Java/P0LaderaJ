package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Player;

public interface IPlayerRepository {
    Player findById(long id);

    Player findByUsername(String username);

    void save(Player move);

    void deleteById(long id);
}
