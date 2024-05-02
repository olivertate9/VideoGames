package dev.profitsoft.videogames.dto.game;

import lombok.Value;

import java.util.List;

/**
 * Represents a DTO with the list of games and totalPages of results.
 */
@Value
public class GameListDTO {

    /**
     * The list of GameInfoDTO objects representing individual games.
     */
    List<GameInfoDTO> games;

    /**
     * The total number of pages in the paginated list of games.
     */
    int totalPages;
}
