package dev.ladera.battleship.model;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ship {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ship.class);

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

    public boolean isValidLocation(int row, int col) {
        return row >= minRow() && row <= maxRow() && col >= minCol() && col <= maxCol();
    }

    public int minRow() {
        return Math.min(rowStart, rowEnd);
    }

    public int maxRow() {
        return Math.max(rowStart, rowEnd);
    }

    public int minCol() {
        return Math.min(colStart, colEnd);
    }

    public int maxCol() {
        return Math.max(colStart, colEnd);
    }

    public int area() {
        return (rowEnd - rowStart + 1) * (colEnd - colStart + 1);
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

    @Override
    public String toString() {
        return "Ship{" + "id="
                + id + ", rowStart="
                + rowStart + ", rowEnd="
                + rowEnd + ", colStart="
                + colStart + ", colEnd="
                + colEnd + ", playerId="
                + playerId + ", gameId="
                + gameId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return Objects.equals(id, ship.id)
                && Objects.equals(rowStart, ship.rowStart)
                && Objects.equals(rowEnd, ship.rowEnd)
                && Objects.equals(colStart, ship.colStart)
                && Objects.equals(colEnd, ship.colEnd)
                && Objects.equals(playerId, ship.playerId)
                && Objects.equals(gameId, ship.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rowStart, rowEnd, colStart, colEnd, playerId, gameId);
    }
}
