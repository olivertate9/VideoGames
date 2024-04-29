package dev.profitsoft.videogames.dto.gameDTOs;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GameSearchDTO {
    private Long developerId;
    private Integer yearReleased;

    @Min(value = 1, message = "Page number must be at least 1")
    private int page;

    @Min(value = 1, message = "Page size must be at least 1")
    private int size;
}
