package dev.ladera.battleship.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.ladera.battleship.dto.PlayerDto;
import dev.ladera.battleship.model.Player;
import dev.ladera.battleship.repository.IGameRepository;
import dev.ladera.battleship.repository.IMoveRepository;
import dev.ladera.battleship.repository.IPlayerRepository;
import dev.ladera.battleship.repository.IShipRepository;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Player mockPlayer = new Player(2L, "user", null, 1L);

        when(playerRepository.findCpuByUsernameAndOrigin(any(String.class), any(Long.class)))
                .thenReturn(null);
        when(playerRepository.save(any(Player.class))).thenReturn(mockPlayer);

        Player player = service.createCpuPlayer(new PlayerDto("username", null, 1L));

        assertNotNull(player);
        assertEquals(player.getId(), mockPlayer.getId());
        assertEquals(player.getUsername(), mockPlayer.getUsername());
        assertEquals(player.getPassphrase(), mockPlayer.getPassphrase());
        assertEquals(player.getOriginPlayerId(), mockPlayer.getOriginPlayerId());
    }
}
