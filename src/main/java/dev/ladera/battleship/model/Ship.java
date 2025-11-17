package dev.ladera.battleship.model;

public class Ship {
    private long id;
    private int rowStart;
    private int rowEnd;
    private int colStart;
    private int colEnd;
    private Long playerId;
    private long gameId;

    public Ship(int rowStart, int rowEnd, int colStart, int colEnd, Long playerId, long gameId) {
        this(-1, rowStart, rowEnd, colStart, colEnd, playerId, gameId);
    }

    public Ship(long id, int rowStart, int rowEnd, int colStart, int colEnd, Long playerId, long gameId) {
        this.id = id;
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
        this.colStart = colStart;
        this.colEnd = colEnd;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public int getRowStart() {
        return rowStart;
    }

    public int getRowEnd() {
        return rowEnd;
    }

    public int getColStart() {
        return colStart;
    }

    public int getColEnd() {
        return colEnd;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public long getGameId() {
        return gameId;
    }
}
