package dev.ladera.battleship.model;

public class Ship {
    private Long id;
    private Integer rowStart;
    private Integer rowEnd;
    private Integer colStart;
    private Integer colEnd;
    private Long playerId;
    private Long gameId;

    public Ship(Integer rowStart, Integer rowEnd, Integer colStart, Integer colEnd, Long playerId, Long gameId) {
        this(null, rowStart, rowEnd, colStart, colEnd, playerId, gameId);
    }

    public Ship(
            Long id, Integer rowStart, Integer rowEnd, Integer colStart, Integer colEnd, Long playerId, Long gameId) {
        this.id = id;
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
        this.colStart = colStart;
        this.colEnd = colEnd;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRowStart() {
        return rowStart;
    }

    public Integer getRowEnd() {
        return rowEnd;
    }

    public Integer getColStart() {
        return colStart;
    }

    public Integer getColEnd() {
        return colEnd;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public Long getGameId() {
        return gameId;
    }
}
