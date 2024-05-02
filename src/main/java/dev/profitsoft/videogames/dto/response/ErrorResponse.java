package dev.profitsoft.videogames.dto.response;

import lombok.Value;

@Value
public class ErrorResponse {
    int status;
    String description;
    String message;
}
