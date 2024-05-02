package dev.profitsoft.videogames.dto.game;

import lombok.Value;

/**
 * Represents a DTO with short info(title and genre) about a game.
 */
@Value
public class GameInfoDTO {
    String title;
    String genre;
}
