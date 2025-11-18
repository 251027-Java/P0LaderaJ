package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Ship;
import java.sql.*;
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

    private Ship toShip(ResultSet rs) throws SQLException {
        return new Ship(
                rs.getLong("id"),
                rs.getInt("row_start"),
                rs.getInt("row_end"),
                rs.getInt("col_start"),
                rs.getInt("col_end"),
                rs.getObject("player_id", Long.class),
                rs.getLong("game_id"));
    }

    @Override
    public Ship findById(long id) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            SELECT id, row_start, row_end, col_start, col_end, player_id, game_id FROM ship
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var rs = st.executeQuery();

            if (rs.next()) {
                return toShip(rs);
            }
        }

        return null;
    }

    @Override
    public List<Ship> findByGameId(long gameId) throws SQLException {
        List<Ship> ret = new ArrayList<>();

        try (var st = connection.prepareStatement(
                """
            SELECT id, row_start, row_end, col_start, col_end, player_id, game_id FROM ship
            WHERE game_id = ?
            """)) {
            st.setLong(1, gameId);

            var rs = st.executeQuery();

            while (rs.next()) {
                ret.add(toShip(rs));
            }
        }

        return ret;
    }

    @Override
    public Ship save(Ship ship) throws SQLException {
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
            st.setObject(5, ship.getPlayerId(), Types.BIGINT);
            st.setLong(6, ship.getGameId());

            var rs = st.executeUpdate();
            var keys = st.getGeneratedKeys();

            if (keys.next()) {
                ship.setId(keys.getLong(1));

                LOGGER.info("Inserted ship ({}): {}", rs, ship.getId());
                return ship;
            }

            return null;
        }
    }

    @Override
    public void deleteById(long id) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            DELETE FROM ship
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var rs = st.executeUpdate();
            LOGGER.info("Deleted ship ({})", rs);
        }
    }
}
