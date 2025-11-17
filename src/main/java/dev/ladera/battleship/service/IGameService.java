package dev.ladera.battleship.service;

import dev.ladera.battleship.dto.GameDto;
import dev.ladera.battleship.dto.MoveDto;
import dev.ladera.battleship.dto.PlayerDto;
import dev.ladera.battleship.dto.ShipDto;
import dev.ladera.battleship.model.Game;
import dev.ladera.battleship.model.Move;
import dev.ladera.battleship.model.Player;
import dev.ladera.battleship.model.Ship;
import java.util.List;

public interface IGameService {
    Player createPlayer(PlayerDto dto);

    Game createGame(GameDto dto);

    Ship createShip(ShipDto dto);

    Move createMove(MoveDto dto);

    List<Game> findGamesByPlayerId(long id);

    List<Ship> findShipsByGameId(long id);

    List<Move> findMovesByGameId(long id);
}
