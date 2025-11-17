package dev.ladera.battleship.model;

public class Move {
    private Long id;
    private Integer turn;
    private Integer row;
    private Integer col;
    private Long playerId;
    private Long gameId;

    public Move(Integer turn, Integer row, Integer col, Long playerId, Long gameId) {
        this(null, turn, row, col, playerId, gameId);
    }

    public Move(Long id, Integer turn, Integer row, Integer col, Long playerId, Long gameId) {
        this.id = id;
        this.turn = turn;
        this.row = row;
        this.col = col;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTurn() {
        return turn;
    }

    public Integer getRow() {
        return row;
    }

    public Integer getCol() {
        return col;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public Long getGameId() {
        return gameId;
    }

    @Override
    public String toString() {
        return "Move{" + "id="
                + id + ", turn="
                + turn + ", row="
                + row + ", col="
                + col + ", playerId="
                + playerId + ", gameId="
                + gameId + '}';
    }
}
