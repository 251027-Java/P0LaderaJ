package dev.ladera.battleship.service;

import dev.ladera.battleship.dto.GameDto;
import dev.ladera.battleship.dto.MoveDto;
import dev.ladera.battleship.dto.PlayerDto;
import dev.ladera.battleship.dto.ShipDto;
import dev.ladera.battleship.model.Game;
import dev.ladera.battleship.model.Move;
import dev.ladera.battleship.model.Player;
import dev.ladera.battleship.model.Ship;
import java.sql.SQLException;
import java.util.List;

public interface IGameService {
    Player createPlayer(PlayerDto dto) throws SQLException;

    Game createGame(GameDto dto) throws SQLException;

    Ship createShip(ShipDto dto) throws SQLException;

    Move createMove(MoveDto dto) throws SQLException;

    List<Game> findGamesByPlayerId(long id) throws SQLException;

    List<Ship> findShipsByGameId(long id) throws SQLException;

    List<Move> findMovesByGameId(long id) throws SQLException;
}
