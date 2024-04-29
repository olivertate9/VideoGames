package dev.profitsoft.videogames.dto.game;

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
