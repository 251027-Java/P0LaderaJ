package dev.ladera.battleship.exception;

public class InvalidPassphraseException extends RuntimeException {
    public InvalidPassphraseException(String message) {
        super(message);
    }
}
