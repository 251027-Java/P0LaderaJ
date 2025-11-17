package dev.ladera.battleship.model;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Game {
    private Long id;
    private Integer rows;
    private Integer cols;

    private List<Move> moves;
    private List<Ship> ships;

    private HashMap<Ship, Integer> shipHealth;
    private HashMap<Long, Integer> shipsRemaining;
    private Long winnerId;

    public Game(Integer rows, Integer cols) {
        this(null, rows, cols);
    }

    public Game(Long id, Integer rows, Integer cols) {
        this.id = id;
        this.rows = rows;
        this.cols = cols;
    }

    private void processMove(Ship ship, Move move) {
        if (Objects.equals(ship.getPlayerId(), move.getPlayerId())) return;

        if (ship.isValidLocation(move.getRow(), move.getCol())) {
            shipHealth.put(ship, shipHealth.get(ship) - 1);

            if (shipHealth.get(ship) == 0) {
                var playerId = ship.getPlayerId();
                shipsRemaining.put(playerId, shipsRemaining.get(playerId) - 1);

                if (shipsRemaining.get(playerId) <= 0) {
                    // TODO set winnerId
                }
            }
        }
    }

    public void simulate() {
        shipHealth = new HashMap<>();
        shipsRemaining = new HashMap<>();

        for (var e : ships) {
            shipHealth.put(e, e.area());

            var playerId = e.getPlayerId();
            shipsRemaining.put(playerId, shipsRemaining.getOrDefault(playerId, 0) + 1);
        }

        moves.sort(null);

        for (var move : moves) {
            for (var ship : ships) {
                processMove(ship, move);
            }
        }
    }

    public boolean isValidLocation(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean hasWinner() {
        return winnerId != null;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public Integer getCols() {
        return cols;
    }

    public Integer getRows() {
        return rows;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Game{" + "id="
                + id + ", rows="
                + rows + ", cols="
                + cols + ", moves="
                + moves + ", ships="
                + ships + '}';
    }
}
