package dev.profitsoft.videogames.exception.exceptions;

/**
 * Exception thrown when a unique value constraint is violated.
 */
public class UniqueValueViolationException extends RuntimeException {

    public UniqueValueViolationException(String message) {
        super(message);
    }
}
