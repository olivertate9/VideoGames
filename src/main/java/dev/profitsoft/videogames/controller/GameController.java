package dev.profitsoft.videogames.controller;

import dev.profitsoft.videogames.dto.game.*;
import dev.profitsoft.videogames.dto.response.RestResponse;
import dev.profitsoft.videogames.service.GameService;
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
    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGame(@PathVariable Long id) {
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
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse> updateGame(@PathVariable Long id, @Valid @RequestBody GameUpdateDTO dto) {
        gameService.updateGame(id, dto);
        return ResponseEntity.ok().body(new RestResponse("Game updated successfully"));
    }

    /**
     * Endpoint to delete a game by its ID.
     *
     * @param id The ID of the game to delete.
     * @return ResponseEntity with a success message and HTTP status 200 (OK).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok().body(new RestResponse("Game deleted"));
    }

    /**
     * Endpoint to retrieve a list of games based on specified filters.
     *
     * @param dto The GameSearchDTO containing filtering criteria.
     * @return ResponseEntity containing a list of games matching the filters.
     */
    @PostMapping("/_list")
    public ResponseEntity<GameListDTO> retrieveGamesByFilters(@RequestBody GameSearchDTO dto) {
        GameListDTO resultDTO = gameService.retrieveGamesByFilters(dto);
        return ResponseEntity.ok(resultDTO);
    }

    /**
     * Endpoint to generate a report based on specified filters that can be downloaded to CSV-file.
     *
     * @param dto      The GameSearchDTO containing filtering criteria for the report.
     * @param response The HttpServletResponse used to write the report as a file.
     */
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
    @PostMapping("/upload")
    public ResponseEntity<GameUploadDTO> uploadGamesFromJsonFile(@RequestParam("file") MultipartFile multipart) {
        GameUploadDTO dto = gameService.uploadGamesFromJsonFile(multipart);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
