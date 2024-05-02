package dev.profitsoft.videogames.dto.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Value;

@Value
public class GameSearchDTO {
    Long developerId;

    @Max(value = 2024, message = "Year Released should be less or equal to {value}")
    Integer yearReleased;

    @Min(value = 1, message = "Page number must be at least {value}")
    int page;

    @Min(value = 1, message = "Page size must be at least {value}")
    int size;
}
