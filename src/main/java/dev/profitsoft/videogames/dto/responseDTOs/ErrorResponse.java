package dev.profitsoft.videogames.dto.responseDTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponse {
    private int status;
    private String description;
    private String message;
}