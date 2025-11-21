package dev.ladera.battleship.exception;

public class MissingOriginPlayerIdException extends RuntimeException {
    public MissingOriginPlayerIdException(String message) {
        super(message);
    }
}
