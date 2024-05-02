package dev.profitsoft.videogames.dto.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * Represents a DTO for updating game information.
 */
@Value
public class GameUpdateDTO {

    /**
     * The title of the game to update (required).
     */
    @NotBlank(message = "title is required")
    String title;

    /**
     * The name of the developer associated with the game (required).
     */
    @NotBlank(message = "developerName is required")
    String developerName;

    /**
     * The year the game was released.
     * From 1958 to 2024 included.
     */
    @Min(value = 1958, message = "Year Released must be at least {value}")
    @Max(value = 2024, message = "Year released should be less than or equal to {value}")
    int yearReleased;

    /**
     * The genre of the game.
     */
    String genre;
}