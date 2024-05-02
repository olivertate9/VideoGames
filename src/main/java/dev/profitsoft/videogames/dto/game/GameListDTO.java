package dev.profitsoft.videogames.dto.game;

import lombok.Value;

import java.util.List;


@Value
public class GameListDTO {
    List<GameInfoDTO> games;
    int totalPages;
}
