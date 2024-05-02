package dev.profitsoft.videogames.dto.game;

import dev.profitsoft.videogames.entity.DeveloperEntity;
import lombok.Value;

@Value
public class GameDTO {
    Long id;
    String title;
    DeveloperEntity developer;
    int yearReleased;
    String genre;
}
