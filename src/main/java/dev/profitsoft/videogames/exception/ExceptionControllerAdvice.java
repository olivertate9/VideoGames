package dev.profitsoft.videogames.exception;

import dev.profitsoft.videogames.dto.response.ErrorResponse;
import dev.profitsoft.videogames.exception.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers.
 */
@RestControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Exception handler for GameNotFoundException.
     *
     * @param e The GameNotFoundException that was thrown.
     * @return ResponseEntity containing an ErrorResponse with HTTP status 404 (Not Found).
     */
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionGameNotFoundHandler(GameNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Exception handler for DeveloperNotFoundException.
     *
     * @param e The DeveloperNotFoundException that was thrown.
     * @return ResponseEntity containing an ErrorResponse with HTTP status 404 (Not Found).
     */
    @ExceptionHandler(DeveloperNotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionDevNotFoundHandler(DeveloperNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Exception handler for MethodArgumentNotValidException (validation errors).
     *
     * @param e The MethodArgumentNotValidException that was thrown.
     * @return ResponseEntity containing an ErrorResponse with HTTP status 400 (Bad Request)
     *         and details about invalid fields.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> exceptionValidationHandler(MethodArgumentNotValidException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, getInvalidFields(e));
    }

    /**
     * Exception handler for UniqueValueViolationException (conflict due to unique constraint).
     *
     * @param e The UniqueValueViolationException that was thrown.
     * @return ResponseEntity containing an ErrorResponse with HTTP status 409 (Conflict).
     */
    @ExceptionHandler(UniqueValueViolationException.class)
    public ResponseEntity<ErrorResponse> exceptionValidationHandler(UniqueValueViolationException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * Exception handler for FileParsingException (error during file parsing).
     *
     * @param e The FileParsingException that was thrown.
     * @return ResponseEntity containing an ErrorResponse with HTTP status 422 (Unprocessable Entity).
     */
    @ExceptionHandler(FileParsingException.class)
    public ResponseEntity<ErrorResponse> exceptionParsingHandler(FileParsingException e) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    /**
     * Exception handler for ReportGeneratingException (error during report generation).
     *
     * @param e The ReportGeneratingException that was thrown.
     * @return ResponseEntity containing an ErrorResponse with HTTP status 500 (Internal Server Error).
     */
    @ExceptionHandler(ReportGeneratingException.class)
    public ResponseEntity<ErrorResponse> exceptionGeneratingReportHandler(ReportGeneratingException e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    /**
     * Builds an ErrorResponse with the specified HTTP status and message.
     *
     * @param status  The HTTP status code.
     * @param message The error message.
     * @return ResponseEntity containing the ErrorResponse with the specified status and message.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, status.getReasonPhrase());
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Extracts invalid field details from a MethodArgumentNotValidException.
     *
     * @param e The MethodArgumentNotValidException.
     * @return Details of invalid fields extracted from the exception.
     */
    private String getInvalidFields(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        sb.append("Validation Error: ");

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage());
            sb.append("; ");
        }
        return sb.toString().trim();
    }
}
