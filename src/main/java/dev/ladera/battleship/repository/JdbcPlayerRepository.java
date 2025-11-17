package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Player;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcPlayerRepository implements IPlayerRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcPlayerRepository.class);

    private final Connection connection;

    public JdbcPlayerRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Player findById(long id) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            SELECT id, username, passphrase, originPlayerId FROM player
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var res = st.executeQuery();

            if (res.next()) {
                return new Player(
                        res.getLong("id"),
                        res.getString("username"),
                        res.getString("passphrase"),
                        res.getObject("originPlayerId", Long.class));
            }
        }

        return null;
    }

    @Override
    public Player findByUsername(String username) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            SELECT id, username, passphrase, originPlayerId FROM player
            WHERE username ILIKE ?
            """)) {
            st.setString(1, username);

            var res = st.executeQuery();

            if (res.next()) {
                return new Player(
                        res.getLong("id"),
                        res.getString("username"),
                        res.getString("passphrase"),
                        res.getObject("originPlayerId", Long.class));
            }
        }

        return null;
    }

    @Override
    public Player save(Player player) throws SQLException {
        try (var st = connection.prepareStatement(
                """
            INSERT INTO player (username, passphrase, originPlayerId)
            VALUES (?, ?, ?)
            """,
                Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, player.getUsername());
            st.setString(2, player.getPassphrase());
            st.setObject(3, player.getOriginPlayerId());

            var res = st.executeUpdate();
            var keys = st.getGeneratedKeys();

            if (keys.next()) {
                player.setId(keys.getLong(1));

                LOGGER.info("Inserted player ({}): {}", res, player.getId());
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

            var res = st.executeUpdate();
            LOGGER.info("Deleted player ({})", res);
        }
    }
}
