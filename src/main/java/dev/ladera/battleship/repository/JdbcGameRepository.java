package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Game;
import java.sql.Connection;
import java.sql.ResultSet;
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

    private Game toGame(ResultSet rs) throws SQLException {
        return new Game(rs.getLong("id"), rs.getInt("rows_val"), rs.getInt("cols_val"));
    }

    @Override
    public Game findById(long id) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            SELECT id, rows_val, cols_val FROM game
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var rs = st.executeQuery();

            if (rs.next()) {
                return toGame(rs);
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

            var rs = st.executeQuery();

            while (rs.next()) {
                ret.add(toGame(rs));
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

            var rs = st.executeUpdate();
            var keys = st.getGeneratedKeys();

            if (keys.next()) {
                game.setId(keys.getLong(1));

                LOGGER.info("Inserted game ({}): {}", rs, game.getId());
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

            var rs = st.executeUpdate();
            LOGGER.info("Deleted game ({})", rs);
        }
    }
}
