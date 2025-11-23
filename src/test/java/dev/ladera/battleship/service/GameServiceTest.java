package dev.ladera.battleship.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    @Mock
    private IGameRepository gameRepository;

    @Mock
    private IPlayerRepository playerRepository;

    @Mock
    private IMoveRepository moveRepository;

    @Mock
    private IShipRepository shipRepository;

    @InjectMocks
    private GameService service;

    @Test
    void createCpuPlayerSuccess() throws SQLException {
        Player mockPlayer = new Player(null, "username", null, 1L);

        when(playerRepository.findCpuByUsernameAndOrigin("username", 1L)).thenReturn(null);
        when(playerRepository.save(mockPlayer)).thenReturn(mockPlayer);

        Player player = service.createCpuPlayer(new PlayerDto("username", null, 1L));

        assertNotNull(player);
        assertEquals(player.getId(), mockPlayer.getId());
        assertEquals(player.getUsername(), mockPlayer.getUsername());
        assertEquals(player.getPassphrase(), mockPlayer.getPassphrase());
        assertEquals(player.getOriginPlayerId(), mockPlayer.getOriginPlayerId());
    }

    @Test
    void createCpuWithNullOrigin() {
        assertThrows(MissingOriginPlayerIdException.class, () -> {
            service.createCpuPlayer(new PlayerDto("", "", null));
        });
    }

    @Test
    void createCpuWithInvalidUsername() {
        assertThrows(InvalidUsernameException.class, () -> {
            service.createCpuPlayer(new PlayerDto("", null, 5L));
        });
    }

    @Test
    void createCpuWithDuplicateUsernameAndOrigin() throws SQLException {
        Player mockPlayer = new Player(null, null, null);
        when(playerRepository.findCpuByUsernameAndOrigin("username1243", 5L)).thenReturn(mockPlayer);

        assertThrows(UsernameExistsException.class, () -> {
            service.createCpuPlayer(new PlayerDto("username1243", null, 5L));
        });
    }

    @Test
    void createPlayerSuccess() throws SQLException {
        Player mockPlayer = new Player(1L, "username1234", "secretpas", null);
        when(playerRepository.findRealByUsername(any())).thenReturn(null);
        when(playerRepository.save(any())).thenReturn(mockPlayer);

        Player player = service.createPlayer(new PlayerDto("username1234", "secretpas", null));

        assertNotNull(player);
        assertEquals(player.getId(), mockPlayer.getId());
        assertEquals(player.getUsername(), mockPlayer.getUsername());
        assertEquals(player.getPassphrase(), mockPlayer.getPassphrase());
        assertEquals(player.getOriginPlayerId(), mockPlayer.getOriginPlayerId());
    }

    @Test
    void createPlayerWithBadUsername() {
        assertThrows(InvalidUsernameException.class, () -> {
            service.createPlayer(new PlayerDto(";,", "", null));
        });
    }

    @Test
    void createPlayerWithBadPassphrase() {
        assertThrows(InvalidPassphraseException.class, () -> {
            service.createPlayer(new PlayerDto("user3248", "a".repeat(100), null));
        });
    }

    @Test
    void createPlayerWithExistingUsername() throws SQLException {
        Player mockPlayer = new Player(null, null, null);
        when(playerRepository.findRealByUsername("username")).thenReturn(mockPlayer);

        assertThrows(UsernameExistsException.class, () -> {
            service.createPlayer(new PlayerDto("username", "water", null));
        });
    }

    @Test
    void createGame() throws SQLException {
        service.createGame(new GameDto(10, 10));

        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void createShipSuccess() throws SQLException {
        Game mockGame = new Game(1L, 10, 10);
        when(gameRepository.findById(1L)).thenReturn(mockGame);

        Ship mockShip = new Ship(null, 1, 1, 1, 1, 1L, 1L);
        when(shipRepository.save(mockShip)).thenReturn(mockShip);

        Ship ship = service.createShip(new ShipDto(1, 1, 1, 1, 1L, 1L));

        assertNotNull(ship);
        assertEquals(ship.getId(), mockShip.getId());
        assertEquals(ship.getRowStart(), mockShip.getRowStart());
        assertEquals(ship.getColStart(), mockShip.getColStart());
        assertEquals(ship.getRowEnd(), mockShip.getRowEnd());
        assertEquals(ship.getColEnd(), mockShip.getColEnd());
        assertEquals(ship.getPlayerId(), mockShip.getPlayerId());
        assertEquals(ship.getGameId(), mockShip.getGameId());
    }

    @Test
    void createShipBadLocation() throws SQLException {
        Game mockGame = new Game(1L, 10, 10);
        when(gameRepository.findById(1L)).thenReturn(mockGame);

        assertThrows(InvalidLocationException.class, () -> {
            service.createShip(new ShipDto(-1, -1, 1, 1, 1L, 1L));
        });
    }

    @Test
    void createMoveSuccess() throws SQLException {
        Game mockGame = new Game(1L, 10, 10);
        when(gameRepository.findById(1L)).thenReturn(mockGame);
        when(moveRepository.findLatestMove(1L)).thenReturn(null);

        Move mockMove = new Move(1, 1, 4, 1L, 1L);
        when(moveRepository.save(mockMove)).thenReturn(mockMove);

        Move move = service.createMove(new MoveDto(1, 1, 4, 1L, 1L));

        assertNotNull(move);
        assertEquals(move.getId(), mockMove.getId());
        assertEquals(move.getTurn(), mockMove.getTurn());
        assertEquals(move.getRow(), mockMove.getRow());
        assertEquals(move.getCol(), mockMove.getCol());
        assertEquals(move.getPlayerId(), mockMove.getPlayerId());
        assertEquals(move.getGameId(), mockMove.getGameId());
    }

    @Test
    void createMoveWithBadLocation() throws SQLException {
        Game mockGame = new Game(1L, 10, 10);
        when(gameRepository.findById(1L)).thenReturn(mockGame);

        assertThrows(InvalidLocationException.class, () -> {
            service.createMove(new MoveDto(1, -1, 1, 1L, 1L));
        });
    }

    @Test
    void createMoveWithBadTurn() throws SQLException {
        Game mockGame = new Game(1L, 10, 10);
        when(gameRepository.findById(1L)).thenReturn(mockGame);

        Move mockMove = new Move(4, 1, 1, 1L, 1L);
        when(moveRepository.findLatestMove(1L)).thenReturn(mockMove);

        assertThrows(InvalidMoveTurnException.class, () -> {
            service.createMove(new MoveDto(2, 1, 1, 2L, 1L));
        });
    }

    @Test
    void createMoveWithBadPlayer() throws SQLException {
        Game mockGame = new Game(1L, 10, 10);
        when(gameRepository.findById(1L)).thenReturn(mockGame);

        Move mockMove = new Move(4, 1, 1, 1L, 1L);
        when(moveRepository.findLatestMove(1L)).thenReturn(mockMove);

        assertThrows(InvalidMovePlayerException.class, () -> {
            service.createMove(new MoveDto(5, 1, 1, 1L, 1L));
        });
    }

    @Test
    void findGamesByPlayerId() throws SQLException {
        service.findGamesByPlayerId(1L);

        verify(gameRepository, times(1)).findByPlayerId(1L);
    }

    @Test
    void findShipsByGameId() throws SQLException {
        service.findShipsByGameId(1L);

        verify(shipRepository, times(1)).findByGameId(1L);
    }

    @Test
    void findMovesByGameId() throws SQLException {
        service.findMovesByGameId(1L);

        verify(moveRepository, times(1)).findByGameId(1L);
    }

    @Test
    void findPlayerByUsername() throws SQLException {
        service.findPlayerByUsername("username");

        verify(playerRepository, times(1)).findRealByUsername("username");
    }

    @Test
    void findCpuByUsernameAndOrigin() throws SQLException {
        service.findCpuByUsernameAndOrigin("username", 2L);

        verify(playerRepository, times(1)).findCpuByUsernameAndOrigin("username", 2L);
    }

    @Test
    void deleteGameById() throws SQLException {
        service.deleteGameById(1L);

        verify(gameRepository, times(1)).deleteById(1L);
    }

    @Test
    void findPlayerById() throws SQLException {
        service.findPlayerById(1L);

        verify(playerRepository, times(1)).findById(1L);
    }
}
