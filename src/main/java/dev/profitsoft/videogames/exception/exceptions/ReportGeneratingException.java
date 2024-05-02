package dev.profitsoft.videogames.exception.exceptions;

/**
 * Exception thrown when an error occurs during report generation.
 */
public class ReportGeneratingException extends RuntimeException {

    public ReportGeneratingException(String message) {
        super(message);
    }
}
