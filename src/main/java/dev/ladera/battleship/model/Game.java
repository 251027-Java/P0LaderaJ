package dev.ladera.battleship.model;

import java.util.List;

public class Game {
    private Long id;
    private Integer rows;
    private Integer cols;
    private List<Move> moves;
    private List<Ship> ships;

    public Game(Integer rows, Integer cols) {
        this(null, rows, cols, null, null);
    }

    public Game(Long id, Integer rows, Integer cols, List<Move> moves, List<Ship> ships) {
        this.id = id;
        this.rows = rows;
        this.cols = cols;
        this.moves = moves;
        this.ships = ships;
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
