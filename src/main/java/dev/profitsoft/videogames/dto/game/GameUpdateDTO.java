package dev.profitsoft.videogames.dto.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class GameUpdateDTO {

    @NotBlank(message = "title is required")
    String title;

    @NotBlank(message = "developerName is required")
    String developerName;

    @Max(value = 2024, message = "Year released should be less than or equal to {value}")
    int yearReleased;

    String genre;
}