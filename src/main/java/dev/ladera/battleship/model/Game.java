package dev.ladera.battleship.model;

import java.util.List;

public class Game {
    private long id;
    private int rows;
    private int cols;
    private List<Move> moves;
    private List<Ship> ships;

    public Game(long id, int rows, int cols, List<Move> moves, List<Ship> ships) {
        this.id = id;
        this.rows = rows;
        this.cols = cols;
        this.moves = moves;
        this.ships = ships;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public long getId() {
        return id;
    }
}
