package dev.ladera.battleship.exception;

public class InvalidMoveTurnException extends RuntimeException {
    public InvalidMoveTurnException(String message) {
        super(message);
    }
}
