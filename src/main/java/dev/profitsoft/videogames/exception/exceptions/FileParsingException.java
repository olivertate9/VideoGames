package dev.profitsoft.videogames.exception.exceptions;

/**
 * Exception thrown when an error occurs when parsing a file.
 */
public class FileParsingException extends RuntimeException {

    public FileParsingException(String message) {
        super(message);
    }
}
