package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Move;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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

    @Override
    public Move findById(long id) {
        try (var st = connection.prepareStatement(
                """
            SELECT id, turn, row_val, col_val, player_id, game_id FROM player_move
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var res = st.executeQuery();

            if (res.next()) {
                return new Move(
                        res.getLong("id"),
                        res.getInt("turn"),
                        res.getInt("row_val"),
                        res.getInt("col_val"),
                        res.getObject("player_id", Long.class),
                        res.getLong("game_id"));
            }

        } catch (SQLException e) {
            LOGGER.error("Error while finding move by id: {}", id, e);
        }

        return null;
    }

    @Override
    public List<Move> findByGameId(long gameId) {
        List<Move> ret = new ArrayList<>();

        try (var st = connection.prepareStatement(
                """
            SELECT id, turn, row_val, col_val, player_id, game_id FROM player_move
            WHERE game_id = ?
            """)) {
            st.setLong(1, gameId);

            var res = st.executeQuery();

            while (res.next()) {
                ret.add(new Move(
                        res.getLong("id"),
                        res.getInt("turn"),
                        res.getInt("row_val"),
                        res.getInt("col_val"),
                        res.getObject("player_id", Long.class),
                        res.getLong("game_id")));
            }

        } catch (SQLException e) {
            LOGGER.error("Error while finding by game id: {}", gameId, e);
        }

        return ret;
    }

    @Override
    public void save(Move move) {
        try (var st = connection.prepareStatement(
                """
            INSERT INTO player_move (turn, row_val, col_val, player_id, game_id)
            VALUES (?, ?, ?, ?, ?)
            """,
                Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, move.getTurn());
            st.setInt(2, move.getRow());
            st.setInt(3, move.getCol());
            st.setObject(4, move.getPlayerId());
            st.setLong(5, move.getGameId());

            var res = st.executeUpdate();
            var keys = st.getGeneratedKeys();

            if (keys.next()) {
                move.setId(keys.getLong(1));
            }

            LOGGER.info("Inserted move ({}): {}", res, move.getId());
        } catch (SQLException e) {
            LOGGER.error("Error while saving move", e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (var st = connection.prepareStatement(
                """
            DELETE FROM player_move
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var res = st.executeUpdate();
            LOGGER.info("Deleted move ({})", res);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting move by id", e);
        }
    }
}
