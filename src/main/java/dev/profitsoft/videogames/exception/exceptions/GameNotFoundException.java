package dev.profitsoft.videogames.exception.exceptions;

/**
 * Exception thrown when a game is not found.
 */
public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(String message) {
        super(message);
    }
}
