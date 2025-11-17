package dev.ladera.battleship.repository;

import dev.ladera.battleship.model.Game;
import dev.ladera.battleship.model.Move;
import dev.ladera.battleship.model.Ship;
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
    public Game findById(long id) {
        try (var st1 = connection.prepareStatement(
                        """
            SELECT rows_val, cols_val FROM game
            WHERE id = ?
            """);
                var st2 = connection.prepareStatement(
                        """
                    SELECT id, turn, row_val, col_val, player_id FROM player_move
                    WHERE game_id = ?
                    """);
                var st3 = connection.prepareStatement(
                        """
                    SELECT id, row_start, row_end, col_start, col_end, player_id FROM ship
                    WHERE game_id = ?
                    """)) {
            st1.setLong(1, id);
            st2.setLong(1, id);
            st3.setLong(1, id);

            var gameRes = st1.executeQuery();
            var moveRes = st2.executeQuery();
            var shipRes = st3.executeQuery();

            if (gameRes.next()) {
                int rows = gameRes.getInt("rows_val");
                int cols = gameRes.getInt("cols_val");

                List<Move> moves = new ArrayList<>();
                List<Ship> ships = new ArrayList<>();

                while (moveRes.next()) {
                    Move move = new Move(
                            moveRes.getLong("id"),
                            moveRes.getInt("turn"),
                            moveRes.getInt("row_val"),
                            moveRes.getInt("col_val"),
                            moveRes.getLong("player_id"),
                            id);
                    moves.add(move);
                }

                while (shipRes.next()) {
                    Ship ship = new Ship(
                            shipRes.getLong("id"),
                            shipRes.getInt("row_start"),
                            shipRes.getInt("row_end"),
                            shipRes.getInt("col_start"),
                            shipRes.getInt("col_end"),
                            shipRes.getLong("player_id"),
                            id);
                    ships.add(ship);
                }

                return new Game(id, rows, cols, moves, ships);
            }

        } catch (SQLException e) {
            LOGGER.error("Error while finding game by id", e);
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
