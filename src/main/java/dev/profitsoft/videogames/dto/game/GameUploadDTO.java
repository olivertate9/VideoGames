package dev.profitsoft.videogames.dto.game;

import lombok.Value;

/**
 * Represents a DTO with summary when uploading games from JSON.
 */
@Value
public class GameUploadDTO {

    /**
     * The number of successfully uploaded games.
     */
    int successUploads;

    /**
     * The number of failed uploads.
     */
    int failUploads;
}
