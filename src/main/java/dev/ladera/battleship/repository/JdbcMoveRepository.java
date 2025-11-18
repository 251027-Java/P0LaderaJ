package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Move;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcMoveRepository implements IMoveRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcMoveRepository.class);

    private final Connection connection;

    public JdbcMoveRepository(Connection connection) {
        this.connection = connection;
    }

    private Move toMove(ResultSet rs) throws SQLException {
        return new Move(
                rs.getLong("id"),
                rs.getInt("turn"),
                rs.getInt("row_val"),
                rs.getInt("col_val"),
                rs.getObject("player_id", Long.class),
                rs.getLong("game_id"));
    }

    @Override
    public Move findById(long id) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            SELECT id, turn, row_val, col_val, player_id, game_id FROM player_move
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var rs = st.executeQuery();

            if (rs.next()) {
                return toMove(rs);
            }
        }

        return null;
    }

    @Override
    public List<Move> findByGameId(long gameId) throws SQLException {
        List<Move> ret = new ArrayList<>();

        try (var st = connection.prepareStatement(
                """
            SELECT id, turn, row_val, col_val, player_id, game_id FROM player_move
            WHERE game_id = ?
            """)) {
            st.setLong(1, gameId);

            var rs = st.executeQuery();

            while (rs.next()) {
                ret.add(toMove(rs));
            }
        }

        return ret;
    }

    @Override
    public Move findLatestMove(long gameId) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            SELECT id, turn, row_val, col_val, player_id, game_id FROM player_move
            WHERE game_id = ?
            ORDER BY turn DESC
            LIMIT 1
            """)) {
            st.setLong(1, gameId);

            var rs = st.executeQuery();

            if (rs.next()) {
                return toMove(rs);
            }

            return null;
        }
    }

    @Override
    public Move save(Move move) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            INSERT INTO player_move (turn, row_val, col_val, player_id, game_id)
            VALUES (?, ?, ?, ?, ?)
            """,
                Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, move.getTurn());
            st.setInt(2, move.getRow());
            st.setInt(3, move.getCol());
            st.setObject(4, move.getPlayerId(), Types.BIGINT);
            st.setLong(5, move.getGameId());

            var rs = st.executeUpdate();
            var keys = st.getGeneratedKeys();

            if (keys.next()) {
                move.setId(keys.getLong(1));

                LOGGER.info("Inserted move ({}): {}", rs, move.getId());
                return move;
            }

            return null;
        }
    }

    @Override
    public void deleteById(long id) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            DELETE FROM player_move
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var rs = st.executeUpdate();
            LOGGER.info("Deleted move ({})", rs);
        }
    }
}
