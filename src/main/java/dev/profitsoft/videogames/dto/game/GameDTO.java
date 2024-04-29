package dev.profitsoft.videogames.dto.game;

import dev.profitsoft.videogames.entity.DeveloperEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GameDTO {
    private Long id;
    private String title;
    private DeveloperEntity developer;
    private int yearReleased;
    private String genre;
}
