package dev.ladera.battleship.model;

public class Move {
    private long id;
    private int turn;
    private int row;
    private int col;
    private long playerId;

    public Move(long id, int turn, int row, int col, long playerId) {
        this.id = id;
        this.turn = turn;
        this.row = row;
        this.col = col;
        this.playerId = playerId;
    }

    public long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public long getPlayerId() {
        return playerId;
    }
}
