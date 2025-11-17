package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Game;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcGameRepository implements IGameRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcGameRepository.class);

    private final Connection connection;

    public JdbcGameRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Game findById(long id) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            SELECT id, rows_val, cols_val FROM game
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var res = st.executeQuery();

            if (res.next()) {
                return new Game(res.getLong("id"), res.getInt("rows_val"), res.getInt("cols_val"), null, null);
            }
        }

        return null;
    }

    @Override
    public List<Game> findByPlayerId(long id) throws SQLException {
        List<Game> ret = new ArrayList<>();

        try (var st = connection.prepareStatement(
                """
                WITH game_players AS (
                    SELECT game_id, player_id FROM player_move
                    UNION ALL
                    SELECT game_id, player_id FROM ship
                ),
                game_filter AS (
                    SELECT DISTINCT game_id FROM game_players
                    WHERE player_id = ?
                )
                SELECT id, rows_val, cols_val FROM game
                WHERE game.id IN (select game_filter.game_id FROM game_filter)
            """)) {
            st.setLong(1, id);

            var res = st.executeQuery();

            while (res.next()) {
                ret.add(new Game(res.getLong("id"), res.getInt("rows_val"), res.getInt("cols_val"), null, null));
            }
        }

        return ret;
    }

    @Override
    public Game save(Game game) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            INSERT INTO game (rows_val, cols_val)
            VALUES (?, ?)
            """,
                Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, game.getRows());
            st.setInt(2, game.getCols());

            var res = st.executeUpdate();
            var keys = st.getGeneratedKeys();

            if (keys.next()) {
                game.setId(keys.getLong(1));

                LOGGER.info("Inserted game ({}): {}", res, game.getId());
                return game;
            }

            return null;
        }
    }

    @Override
    public void deleteById(long id) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            DELETE FROM game
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var res = st.executeUpdate();
            LOGGER.info("Deleted game ({})", res);
        }
    }
}
