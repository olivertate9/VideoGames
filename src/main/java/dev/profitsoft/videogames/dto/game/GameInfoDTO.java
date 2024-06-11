package dev.profitsoft.videogames.dto.game;

import lombok.Value;

/**
 * Represents a DTO with short info(id, title and genre) about a game.
 */
@Value
public class GameInfoDTO {
    Long id;
    String title;
    String genre;
}
