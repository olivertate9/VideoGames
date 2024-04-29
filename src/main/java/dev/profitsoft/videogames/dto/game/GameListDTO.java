package dev.profitsoft.videogames.dto.game;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GameListDTO {
    private List<GameInfoDTO> games;
    private int totalPages;
}
