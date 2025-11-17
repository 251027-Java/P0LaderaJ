package dev.ladera.battleship.model;

public class Move {
    private long id;
    private int turn;
    private int row;
    private int col;
    private Long playerId;
    private long gameId;

    public Move(int turn, int row, int col, Long playerId, long gameId) {
        this(-1, turn, row, col, playerId, gameId);
    }

    public Move(long id, int turn, int row, int col, Long playerId, long gameId) {
        this.id = id;
        this.turn = turn;
        this.row = row;
        this.col = col;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public void setId(long id) {
        this.id = id;
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

    public Long getPlayerId() {
        return playerId;
    }

    public long getGameId() {
        return gameId;
    }
}
