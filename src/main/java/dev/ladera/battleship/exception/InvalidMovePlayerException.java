package dev.ladera.battleship.exception;

public class InvalidMovePlayerException extends RuntimeException {
    public InvalidMovePlayerException(String message) {
        super(message);
    }
}
