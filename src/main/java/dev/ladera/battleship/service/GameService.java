package dev.ladera.battleship.service;

import dev.ladera.battleship.dto.GameDto;
import dev.ladera.battleship.dto.MoveDto;
import dev.ladera.battleship.dto.PlayerDto;
import dev.ladera.battleship.dto.ShipDto;
import dev.ladera.battleship.exception.*;
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
import java.util.Objects;
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

    private boolean isValidUsername(String username) {
        return username.matches("[a-zA-Z0-9]{3,30}");
    }

    private boolean isValidPassphrase(String passphrase) {
        return passphrase.length() >= 5 && passphrase.length() <= 50;
    }

    @Override
    public Player createCpuPlayer(PlayerDto dto) throws SQLException {
        if (!isValidUsername(dto.username())) {
            throw new InvalidUsernameException("Username must only contain 3-30 alphanumeric characters");
        }

        if (playerRepository.findCpuByUsernameAndOrigin(dto.username(), dto.originPlayerId()) != null) {
            throw new UsernameExistsException("Username already exists");
        }

        return playerRepository.save(new Player(dto.username(), dto.passphrase(), dto.originPlayerId()));
    }

    @Override
    public Player createPlayer(PlayerDto dto) throws SQLException {
        if (!isValidUsername(dto.username())) {
            throw new InvalidUsernameException("Username must only contain 3-30 alphanumeric characters");
        }

        if (!isValidPassphrase(dto.passphrase())) {
            throw new InvalidPassphraseException("Passphrase must be between 5 and 50 characters");
        }

        if (playerRepository.findRealByUsername(dto.username()) != null) {
            throw new UsernameExistsException("Username already exists");
        }

        return playerRepository.save(new Player(dto.username(), dto.passphrase(), dto.originPlayerId()));
    }

    @Override
    public Game createGame(GameDto dto) throws SQLException {
        return gameRepository.save(new Game(dto.rows(), dto.cols()));
    }

    @Override
    public Ship createShip(ShipDto dto) throws SQLException {
        Game game = gameRepository.findById(dto.gameId());

        if (!game.isValidLocation(dto.rowStart(), dto.colStart())
                || !game.isValidLocation(dto.rowEnd(), dto.colEnd())) {
            throw new InvalidLocationException("Ship location is out of bounds");
        }

        return shipRepository.save(
                new Ship(dto.rowStart(), dto.rowEnd(), dto.colStart(), dto.colEnd(), dto.playerId(), dto.gameId()));
    }

    private boolean isValidTurn(Move latestMove, int turn) {
        if (latestMove == null) {
            return turn == 1;
        }

        // TODO weird situation
        if (latestMove.getTurn() == null) return false;

        return latestMove.getTurn() + 1 == turn;
    }

    private boolean isValidPlayer(Move latestMove, Long playerId) {
        if (latestMove == null) {
            return true;
        }

        return Objects.equals(latestMove.getPlayerId(), playerId);
    }

    @Override
    public Move createMove(MoveDto dto) throws SQLException {
        Game game = gameRepository.findById(dto.gameId());

        if (!game.isValidLocation(dto.row(), dto.col())) {
            throw new InvalidLocationException("Move location is out of bounds");
        }

        Move latestMove = moveRepository.findLatestMove(dto.gameId());

        if (!isValidTurn(latestMove, dto.turn())) {
            throw new InvalidMoveTurnException("Move's turn is out of order");
        }

        if (!isValidPlayer(latestMove, dto.playerId())) {
            throw new InvalidMovePlayerException("Player is moving out of order");
        }

        return moveRepository.save(new Move(dto.turn(), dto.row(), dto.col(), dto.playerId(), dto.gameId()));
    }

    @Override
    public List<Game> findGamesByPlayerId(long id) throws SQLException {
        return gameRepository.findByPlayerId(id);
    }

    @Override
    public List<Ship> findShipsByGameId(long id) throws SQLException {
        return shipRepository.findByGameId(id);
    }

    @Override
    public List<Move> findMovesByGameId(long id) throws SQLException {
        return moveRepository.findByGameId(id);
    }

    @Override
    public Player findPlayerByUsername(String username) throws SQLException {
        return playerRepository.findRealByUsername(username);
    }

    @Override
    public Player findCpuByUsernameAndOrigin(String username, long originPlayerId) throws SQLException {
        return playerRepository.findCpuByUsernameAndOrigin(username, originPlayerId);
    }
}
