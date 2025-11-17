package dev.ladera.battleship.model;

import java.util.Objects;

public class Move implements Comparable<Move> {
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

    @Override
    public int compareTo(Move o) {
        if (o.turn == null) return -1;
        if (turn == null) return 1;
        return turn - o.turn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(id, move.id)
                && Objects.equals(turn, move.turn)
                && Objects.equals(row, move.row)
                && Objects.equals(col, move.col)
                && Objects.equals(playerId, move.playerId)
                && Objects.equals(gameId, move.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, turn, row, col, playerId, gameId);
    }
}
