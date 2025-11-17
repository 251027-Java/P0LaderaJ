package dev.ladera.battleship.model;

import java.util.List;

public class Game {
    private long id;
    private int rows;
    private int cols;
    private List<Move> moves;
    private List<Ship> ships;
}
