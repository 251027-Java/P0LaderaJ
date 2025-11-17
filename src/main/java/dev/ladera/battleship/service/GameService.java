package dev.ladera.battleship.service;

import dev.ladera.battleship.dto.GameDto;
import dev.ladera.battleship.dto.MoveDto;
import dev.ladera.battleship.dto.PlayerDto;
import dev.ladera.battleship.dto.ShipDto;
import dev.ladera.battleship.model.Game;
import dev.ladera.battleship.model.Move;
import dev.ladera.battleship.model.Player;
import dev.ladera.battleship.model.Ship;
import dev.ladera.battleship.repository.IGameRepository;
import dev.ladera.battleship.repository.IMoveRepository;
import dev.ladera.battleship.repository.IPlayerRepository;
import dev.ladera.battleship.repository.IShipRepository;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameService implements IGameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    private final IGameRepository gameRepository;
    private final IPlayerRepository playerRepository;
    private final IMoveRepository moveRepository;
    private final IShipRepository shipRepository;

    public GameService(
            IGameRepository gameRepository,
            IPlayerRepository playerRepository,
            IMoveRepository moveRepository,
            IShipRepository shipRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.moveRepository = moveRepository;
        this.shipRepository = shipRepository;
    }

    @Override
    public Player createPlayer(PlayerDto dto) {
        try {
            if (playerRepository.findByUsername(dto.username()) != null) {
                LOGGER.info("Tried creating player with existing username: {}", dto.username());
                return null;
            }

            return playerRepository.save(new Player(dto.username(), dto.passphrase(), dto.originPlayerId()));
        } catch (SQLException e) {
            LOGGER.error("Error occurred during player creation: {}", dto, e);
            return null;
        }
    }

    @Override
    public Game createGame(GameDto dto) {
        try {
            return gameRepository.save(new Game(dto.rows(), dto.cols()));
        } catch (SQLException e) {
            LOGGER.error("Error occurred during game creation: {}", dto, e);
            return null;
        }
    }

    @Override
    public Ship createShip(ShipDto dto) {
        try {
            Game game = gameRepository.findById(dto.gameId());

            if (!game.isValidLocation(dto.rowStart(), dto.colStart())
                    || !game.isValidLocation(dto.rowEnd(), dto.colEnd())) {
                LOGGER.info("Tried creating ship out of bounds of game: {} {}", dto, game);
                return null;
            }

            return shipRepository.save(
                    new Ship(dto.rowStart(), dto.rowEnd(), dto.colStart(), dto.colEnd(), dto.playerId(), dto.gameId()));
        } catch (SQLException e) {
            LOGGER.error("Error occurred during ship creation: {}", dto, e);
            return null;
        }
    }

    private boolean isValidTurn(Move latestMove, int turn) {
        if (latestMove == null) {
            return turn == 1;
        }

        if (latestMove.getTurn() == null) return false;
        return latestMove.getTurn() + 1 == turn;
    }

    @Override
    public Move createMove(MoveDto dto) {
        try {
            Game game = gameRepository.findById(dto.gameId());

            if (!game.isValidLocation(dto.row(), dto.col())) {
                LOGGER.info("Tried creating move out of bounds of game: {} {}", dto, game);
                return null;
            }

            Move latestMove = moveRepository.findLatestMove(dto.gameId());

            if (!isValidTurn(latestMove, dto.turn())) {
                LOGGER.info("Tried creating move turn out of order: {} {}", dto, latestMove);
                return null;
            }

            return moveRepository.save(new Move(dto.turn(), dto.row(), dto.col(), dto.playerId(), dto.gameId()));
        } catch (SQLException e) {
            LOGGER.error("Error occurred during move creation: {}", dto, e);
            return null;
        }
    }

    @Override
    public List<Game> findGamesByPlayerId(long id) {
        try {
            return gameRepository.findByPlayerId(id);
        } catch (SQLException e) {
            LOGGER.error("Error occurred while finding games by player id: {}", id, e);
            return null;
        }
    }

    @Override
    public List<Ship> findShipsByGameId(long id) {
        try {
            return shipRepository.findByGameId(id);
        } catch (SQLException e) {
            LOGGER.error("Error occurred while finding ships by game id: {}", id, e);
            return null;
        }
    }

    @Override
    public List<Move> findMovesByGameId(long id) {
        try {
            return moveRepository.findByGameId(id);
        } catch (SQLException e) {
            LOGGER.error("Error occurred while finding moves by game id: {}", id, e);
            return null;
        }
    }
}
