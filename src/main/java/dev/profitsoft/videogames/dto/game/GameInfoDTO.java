package dev.profitsoft.videogames.dto.game;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GameInfoDTO {
    private String title;
    private String genre;
}
