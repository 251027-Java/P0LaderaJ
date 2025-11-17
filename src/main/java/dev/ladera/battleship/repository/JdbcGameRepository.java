package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Game;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcGameRepository implements IGameRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcGameRepository.class);

    private final Connection connection;

    public JdbcGameRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Game findById(long id) {
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
        } catch (SQLException e) {
            LOGGER.error("Error while finding game by id: {}", id, e);
        }

        return null;
    }

    @Override
    public void save(Game game) {
        try (var st = connection.prepareStatement(
                """
            INSERT INTO game
            VALUES (?, ?)
            """,
                Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, game.getRows());
            st.setInt(2, game.getCols());

            var res = st.executeUpdate();
            var keys = st.getGeneratedKeys();

            if (keys.next()) {
                game.setId(keys.getLong(1));
            }

            LOGGER.info("Inserted game ({}): {}", res, game.getId());
        } catch (SQLException e) {
            LOGGER.error("Error while saving game", e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (var st = connection.prepareStatement(
                """
            DELETE FROM game
            WHERE id = ?
            """)) {
            st.setLong(1, id);

            var res = st.executeUpdate();
            LOGGER.info("Deleted game ({})", res);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting game by id", e);
        }
    }
}
