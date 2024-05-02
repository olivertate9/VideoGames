package dev.profitsoft.videogames.controller;

import dev.profitsoft.videogames.dto.game.*;
import dev.profitsoft.videogames.dto.response.RestResponse;
import dev.profitsoft.videogames.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing games.
 */
@Tag(name = "Game", description = "Game management APIs")
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * Endpoint to add a new game.
     *
     * @param dto The GameUpdateDTO representing the game to be added.
     * @return ResponseEntity containing the saved GameUpdateDTO with HTTP status 201 (Created).
     */
    @Operation(
            summary = "Add a new game",
            description = "Adds a new game to the system."
    )
    @ApiResponse(responseCode = "201", description = "Game added successfully")
    @PostMapping
    public ResponseEntity<GameUpdateDTO> addGame(@Valid @RequestBody GameUpdateDTO dto) {
        GameUpdateDTO savedGame = gameService.saveGame(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedGame);
    }

    /**
     * Endpoint to retrieve a game by its ID.
     *
     * @param id The ID of the game to retrieve.
     * @return ResponseEntity containing the retrieved GameDTO with HTTP status 200 (OK).
     */
    @Operation(
            summary = "Retrieve a game by ID",
            description = "Retrieves a game based on its unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGame(
            @Parameter(
                    description = "ID of the game to retrieve",
                    required = true
            )
            @PathVariable Long id) {
        GameDTO game = gameService.getGame(id);
        return ResponseEntity.ok(game);
    }

    /**
     * Endpoint to update an existing game.
     *
     * @param id  The ID of the game to update.
     * @param dto The GameUpdateDTO containing updated game information.
     * @return ResponseEntity with a success message and HTTP status 200 (OK).
     */
    @Operation(
            summary = "Update a game",
            description = "Updates an existing game based on its ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse> updateGame(
            @Parameter(
                    description = "ID of the game to update",
                    required = true
            )
            @PathVariable Long id,
            @Valid @RequestBody GameUpdateDTO dto) {
        gameService.updateGame(id, dto);
        return ResponseEntity.ok().body(new RestResponse("Game updated successfully"));
    }

    /**
     * Endpoint to delete a game by its ID.
     *
     * @param id The ID of the game to delete.
     * @return ResponseEntity with a success message and HTTP status 200 (OK).
     */
    @Operation(
            summary = "Delete a game",
            description = "Deletes a game based on its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse> deleteGame(
            @Parameter(
                    description = "ID of the game to delete",
                    required = true
            )
            @PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok().body(new RestResponse("Game deleted"));
    }

    /**
     * Endpoint to retrieve a list of games based on specified filters.
     * Optional filters: developerId, yearReleased
     *
     * @param dto The GameSearchDTO containing filtering criteria.
     * @return ResponseEntity containing a list of games matching the filters.
     */
    @Operation(
            summary = "Retrieve games by optional filters: developerId, yearReleased",
            description = "Retrieves a list of games based on specified filters."
    )
    @PostMapping("/_list")
    public ResponseEntity<GameListDTO> retrieveGamesByFilters(@RequestBody GameSearchDTO dto) {
        GameListDTO resultDTO = gameService.retrieveGamesByFilters(dto);
        return ResponseEntity.ok(resultDTO);
    }

    /**
     * Endpoint to generate a report based on specified filters that can be downloaded to CSV-file.
     * Optional filters: developerId, yearReleased
     *
     * @param dto      The GameSearchDTO containing filtering criteria for the report.
     * @param response The HttpServletResponse used to write the report as a file.
     */
    @Operation(
            summary = "Generate report by optional filters: developerId, yearReleased",
            description = "Generates a report based on specified filters that can be downloaded as a CSV file."
    )
    @PostMapping(value = "/_report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void generateReportByFilters(@RequestBody GameSearchDTO dto, HttpServletResponse response) {
        gameService.generateReport(dto, response);
    }

    /**
     * Endpoint to upload games from a JSON file.
     *
     * @param multipart The MultipartFile containing the JSON file to upload.
     * @return ResponseEntity containing information about the uploaded games.
     */
    @Operation(
            summary = "Upload games from JSON file",
            description = "Uploads games from a JSON file."
    )
    @ApiResponse(responseCode = "201", description = "Games from JSON-file added successfully")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GameUploadDTO> uploadGamesFromJsonFile(
            @Parameter(
                    description = "JSON file containing game data",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart("file") MultipartFile multipart) {
        GameUploadDTO dto = gameService.uploadGamesFromJsonFile(multipart);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
