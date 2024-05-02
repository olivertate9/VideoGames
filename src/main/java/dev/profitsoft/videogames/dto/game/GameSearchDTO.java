package dev.profitsoft.videogames.dto.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Value;

/**
 * Represents a DTO for specifying search criteria when retrieving games.
 */
@Value
public class GameSearchDTO {
    Long developerId;

    /**
     * The year the game was released.
     * The minimum and maximum year of release to filter games by 1958-2024.
     */
    @Min(value = 1958, message = "Year Released must be at least {value}")
    @Max(value = 2024, message = "Year Released should be less or equal to {value}")
    Integer yearReleased;

    /**
     * The page number for pagination (1-based index).
     * Must be at least 1.
     */
    @Min(value = 1, message = "Page number must be at least {value}")
    int page;

    /**
     * The page size for pagination.
     * Must be at least 1.
     */
    @Min(value = 1, message = "Page size must be at least {value}")
    int size;
}
