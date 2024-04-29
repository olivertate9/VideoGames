package dev.profitsoft.videogames.controller;

import dev.profitsoft.videogames.dto.responseDTOs.ErrorResponse;
import dev.profitsoft.videogames.exception.NotFoundException;
import dev.profitsoft.videogames.exception.UniqueValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionNotFoundHandler(NotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> exceptionValidationHandler(MethodArgumentNotValidException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, getInvalidFields(e));
    }

    @ExceptionHandler(UniqueValueException.class)
    public ResponseEntity<ErrorResponse> exceptionValidationHandler(UniqueValueException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .description(status.getReasonPhrase())
                .build());
    }

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
