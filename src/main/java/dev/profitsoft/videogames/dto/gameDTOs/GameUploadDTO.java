package dev.profitsoft.videogames.dto.gameDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameUploadDTO {
    private int successUploads;
    private int failUploads;
}
