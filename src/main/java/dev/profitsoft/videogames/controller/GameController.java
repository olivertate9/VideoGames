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

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GameUpdateDTO> addGame(@Valid @RequestBody GameUpdateDTO gameUpdateDto) {
        GameUpdateDTO savedGame = gameService.saveGame(gameUpdateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedGame);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGame(@PathVariable Long id) {
        GameDTO game = gameService.getGame(id);
        return ResponseEntity.ok(game);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestResponse> updateGame(@PathVariable Long id, @Valid @RequestBody GameUpdateDTO gameUpdateDto) {
        gameService.updateGame(id, gameUpdateDto);
        return ResponseEntity.ok().body(new RestResponse("Game updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok().body(new RestResponse("Game deleted"));
    }

    @PostMapping("/_list")
    public ResponseEntity<GameListDTO> findGamesWithFilters(@RequestBody GameSearchDTO gameSearchDTO) {
        GameListDTO resultDTO = gameService.searchGamesWithFilters(gameSearchDTO);
        return ResponseEntity.ok(resultDTO);
    }

    @PostMapping(value = "/_report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void generateReport(@RequestBody GameSearchDTO gameSearchDTO, HttpServletResponse response) {
        gameService.generateReport(gameSearchDTO, response);
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GameUploadDTO> uploadFromFile(@RequestParam("file") MultipartFile multipart) {
        GameUploadDTO gameUploadDTO = gameService.uploadFromFile(multipart);
        return ResponseEntity.ok().body(gameUploadDTO);
    }
}
