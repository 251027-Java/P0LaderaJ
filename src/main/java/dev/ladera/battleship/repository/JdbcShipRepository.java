package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Ship;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcShipRepository implements IShipRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcShipRepository.class);
    private final Connection connection;

    public JdbcShipRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Ship findById(long id) {
        try (var st = connection.prepareStatement(
                """
            SELECT id, row_start, row_end, col_start, col_end, player_id, game_id FROM ship
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var res = st.executeQuery();

            if (res.next()) {
                return new Ship(
                        res.getLong("id"),
                        res.getInt("row_start"),
                        res.getInt("row_end"),
                        res.getInt("col_start"),
                        res.getInt("col_end"),
                        res.getObject("player_id", Long.class),
                        res.getLong("game_id"));
            }

        } catch (SQLException e) {
            LOGGER.error("Error while finding ship by id: {}", id, e);
        }

        return null;
    }

    @Override
    public List<Ship> findByGameId(long gameId) {
        List<Ship> ret = new ArrayList<>();

        try (var st = connection.prepareStatement(
                """
            SELECT id, row_start, row_end, col_start, col_end, player_id, game_id FROM ship
            WHERE game_id = ?
            """)) {
            st.setLong(1, gameId);

            var res = st.executeQuery();

            while (res.next()) {
                ret.add(new Ship(
                        res.getLong("id"),
                        res.getInt("row_start"),
                        res.getInt("row_end"),
                        res.getInt("col_start"),
                        res.getInt("col_end"),
                        res.getObject("player_id", Long.class),
                        res.getLong("game_id")));
            }

        } catch (SQLException e) {
            LOGGER.error("Error while finding by game id: {}", gameId, e);
        }

        return ret;
    }

    @Override
    public void save(Ship ship) {
        try (var st = connection.prepareStatement(
                """
            INSERT INTO ship (row_start, row_end, col_start, col_end, player_id, game_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """,
                Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, ship.getRowStart());
            st.setInt(2, ship.getRowEnd());
            st.setInt(3, ship.getColStart());
            st.setInt(4, ship.getColEnd());
            st.setLong(5, ship.getPlayerId());
            st.setLong(6, ship.getGameId());

            var res = st.executeUpdate();
            var keys = st.getGeneratedKeys();

            if (keys.next()) {
                ship.setId(keys.getLong(1));
            }

            LOGGER.info("Inserted ship ({}): {}", res, ship.getId());
        } catch (SQLException e) {
            LOGGER.error("Error while saving ship", e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (var st = connection.prepareStatement(
                """
            DELETE FROM ship
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var res = st.executeUpdate();
            LOGGER.info("Deleted ship ({})", res);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting ship by id", e);
        }
    }
}
