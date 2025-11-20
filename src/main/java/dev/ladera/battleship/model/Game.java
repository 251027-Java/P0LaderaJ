package dev.ladera.battleship.model;

import java.util.*;

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
        moves = new ArrayList<>();
        ships = new ArrayList<>();
        shipHealth = new HashMap<>();
        shipsRemaining = new HashMap<>();
    }

    private void processMove(Ship ship, Move move) {
        // ensure the shipowner and move initiator are from different players
        if (Objects.equals(ship.getPlayerId(), move.getPlayerId())) return;

        // check if ship is already destroyed
        if (shipHealth.get(ship) <= 0) return;

        // check if hit
        if (ship.isValidLocation(move.getRow(), move.getCol())) {
            shipHealth.put(ship, shipHealth.get(ship) - 1);

            if (shipHealth.get(ship) > 0) return;

            // ship is destroyed
            var playerId = ship.getPlayerId();
            shipsRemaining.put(playerId, shipsRemaining.get(playerId) - 1);

            if (shipsRemaining.get(playerId) > 0) return;

            // player ran out of ships, so check if there's a winner
            List<Long> alivePlayers = shipsRemaining.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .toList();

            if (alivePlayers.size() == 1) {
                winnerId = alivePlayers.getFirst();
            }
        }
    }

    public void simulate() {
        shipHealth = new HashMap<>();
        shipsRemaining = new HashMap<>();

        ships.forEach(this::processShip);
        moves.sort(null);

        for (var move : moves) {
            ships.forEach(e -> processMove(e, move));
        }
    }

    private void processShip(Ship ship) {
        shipHealth.put(ship, ship.area());

        var playerId = ship.getPlayerId();
        shipsRemaining.put(playerId, shipsRemaining.getOrDefault(playerId, 0) + 1);
    }

    public void addMove(Move move) {
        moves.add(move);
        ships.forEach(e -> processMove(e, move));
    }

    public void addShip(Ship ship) {
        ships.add(ship);
        processShip(ship);
    }

    public boolean isValidLocation(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean hasWinner() {
        return winnerId != null;
    }

    public Long getWinnerId() {
        return winnerId;
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
