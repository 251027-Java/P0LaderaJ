package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Player;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcPlayerRepository implements IPlayerRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcPlayerRepository.class);

    private final Connection connection;

    public JdbcPlayerRepository(Connection connection) {
        this.connection = connection;
    }

    private Player toPlayer(ResultSet rs) throws SQLException {
        return new Player(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("passphrase"),
                rs.getObject("origin_player_id", Long.class));
    }

    @Override
    public Player findById(long id) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            SELECT id, username, passphrase, origin_player_id FROM player
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var rs = st.executeQuery();

            if (rs.next()) {
                return toPlayer(rs);
            }
        }

        return null;
    }

    @Override
    public Player findByUsername(String username) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            SELECT id, username, passphrase, origin_player_id FROM player
            WHERE username ILIKE ?
            """)) {
            st.setString(1, username);

            var rs = st.executeQuery();

            if (rs.next()) {
                return toPlayer(rs);
            }
        }

        return null;
    }

    @Override
    public Player save(Player player) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            INSERT INTO player (username, passphrase, origin_player_id)
            VALUES (?, ?, ?)
            """,
                Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, player.getUsername());
            st.setString(2, player.getPassphrase());
            st.setObject(3, player.getOriginPlayerId(), Types.BIGINT);

            var rs = st.executeUpdate();
            var keys = st.getGeneratedKeys();

            if (keys.next()) {
                player.setId(keys.getLong(1));

                LOGGER.info("Inserted player ({}): {}", rs, player.getId());
                return player;
            }

            return null;
        }
    }

    @Override
    public void deleteById(long id) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            DELETE FROM player
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var rs = st.executeUpdate();
            LOGGER.info("Deleted player ({})", rs);
        }
    }
}
