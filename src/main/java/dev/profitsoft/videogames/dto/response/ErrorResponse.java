package dev.profitsoft.videogames.dto.response;

import lombok.Value;

/**
 * Represents a DTO for error responses.
 */
@Value
public class ErrorResponse {

    /**
     * The HTTP status code associated with the error.
     */
    int status;

    /**
     * A description of the error.
     */
    String description;

    /**
     * A detailed error message.
     */
    String message;
}
