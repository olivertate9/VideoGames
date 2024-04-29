package dev.profitsoft.videogames.dto.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameUpdateDTO {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "developerName is required")
    private String developerName;

    private int yearReleased;
    private String genre;
}