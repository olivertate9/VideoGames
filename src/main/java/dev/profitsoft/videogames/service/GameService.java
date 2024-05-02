package dev.profitsoft.videogames.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.profitsoft.videogames.dto.game.*;
import dev.profitsoft.videogames.entity.DeveloperEntity;
import dev.profitsoft.videogames.entity.GameEntity;
import dev.profitsoft.videogames.exception.exceptions.DeveloperNotFoundException;
import dev.profitsoft.videogames.exception.exceptions.FileParsingException;
import dev.profitsoft.videogames.exception.exceptions.GameNotFoundException;
import dev.profitsoft.videogames.exception.exceptions.ReportGeneratingException;
import dev.profitsoft.videogames.mapper.GameMapper;
import dev.profitsoft.videogames.repository.GameRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing game-related operations.
 */
@Service
@AllArgsConstructor
public class GameService {

    /**
     * Represents the header value for a CSV file attachment response.
     * It specifies the filename of the attachment as "games.csv".
     */
    private static final String HEADER = "attachment; filename=games.csv";

    private final GameRepository gameRepository;
    private final DeveloperService developerService;
    private final GameMapper gameMapper;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * Saves a new game in the database.
     *
     * @param dto The game data to be saved.
     * @return The saved game representation.
     * @throws DeveloperNotFoundException If the developer provided does not exist.
     */
    public GameUpdateDTO saveGame(GameUpdateDTO dto) {
        GameEntity gameEntity = createGameEntityFromDTO(dto);
        GameEntity savedGame = gameRepository.save(gameEntity);
        return gameMapper.toGameUpdateDTO(savedGame);
    }

    /**
     * Retrieves a game by ID.
     *
     * @param id the ID of the game to retrieve.
     * @return The retrieved game representation.
     * @throws GameNotFoundException If the game with provided ID is not found.
     */
    public GameDTO getGame(Long id) {
        GameEntity gameEntity = getGameByIdOrThrow(id);
        return gameMapper.toGameDTO(gameEntity);
    }

    /**
     * Updates an existing game in the database.
     *
     * @param id  The ID of the game to update.
     * @param dto The updated data for game.
     * @throws GameNotFoundException      If the game with provided ID is not found.
     * @throws DeveloperNotFoundException If the developer provided does not exist.
     */
    public void updateGame(Long id, GameUpdateDTO dto) {
        GameEntity gameEntity = getGameByIdOrThrow(id);
        updateValues(dto, gameEntity);
        gameRepository.save(gameEntity);
    }

    /**
     * Deletes a game from the database.
     *
     * @param id The ID of the game to delete.
     * @throws GameNotFoundException If the game with provided ID is not found.
     */
    public void deleteGame(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new GameNotFoundException("Game with id %d not found".formatted(id));
        }
        gameRepository.deleteById(id);
    }

    /**
     * Retrieves a list of games based on search filters.
     *
     * @param dto The search filters.
     * @return The list of games matching the filters and total pages of searched results.
     */
    public GameListDTO retrieveGamesByFilters(GameSearchDTO dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage() - 1, dto.getSize());
        Page<GameEntity> gamesPage = gameRepository.findGamesWithFilters(dto.getDeveloperId(), dto.getYearReleased(), pageRequest);
        List<GameInfoDTO> gameInfoDTOList = gamesPage.map(gameMapper::toGameInfoDTO).toList();

        return new GameListDTO(gameInfoDTOList, gamesPage.getTotalPages());
    }

    /**
     * Generates a report of games based on search filters and writes it to the HTTP response.
     *
     * @param dto      The search filters.
     * @param response The HTTP response object.
     * @throws ReportGeneratingException If there was an error generating report.
     */
    public void generateReport(GameSearchDTO dto, HttpServletResponse response) {
        try {
            setResponseHeaders(response);
            List<GameEntity> gameList = gameRepository.findAllForReport(dto.getDeveloperId(), dto.getYearReleased());
            String reportData = generateReportData(gameList);
            writeReportToResponse(response, reportData);
        } catch (IOException e) {
            throw new ReportGeneratingException(e.getMessage());
        }
    }

    /**
     * Uploads games from a JSON file to the database.
     *
     * @param file The JSON file containing game data.
     * @return The upload summary.
     * @throws FileParsingException if there was an error processing file.
     */
    public GameUploadDTO uploadGamesFromJsonFile(MultipartFile file) {
        List<GameEntity> gamesToSave = new ArrayList<>();
        int invalidDto = 0;

        List<GameUpdateDTO> dtoList = convertFileToDTOList(file);

        for (GameUpdateDTO dto : dtoList) {
            if (!isValid(dto)) {
                invalidDto++;
                continue;
            }
            try {
                GameEntity gameEntity = createGameEntityFromDTO(dto);
                gamesToSave.add(gameEntity);
            } catch (DeveloperNotFoundException e) {
                invalidDto++;
            }
        }

        gameRepository.saveAll(gamesToSave);
        return new GameUploadDTO(gamesToSave.size(), invalidDto);
    }

    private void setResponseHeaders(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, HEADER);
    }

    private String generateReportData(List<GameEntity> gameList) {
        StringBuilder sb = new StringBuilder();
        sb.append("Title;Genre\n");
        for (GameEntity gameEntity : gameList) {
            sb.append(gameEntity.getTitle());
            sb.append(";");
            sb.append(gameEntity.getGenre());
            sb.append("\n");
        }
        return sb.toString();
    }

    private void writeReportToResponse(HttpServletResponse response, String reportData) throws IOException {
        response.getOutputStream().write(reportData.getBytes(StandardCharsets.UTF_8));
        response.getOutputStream().flush();
    }

    private GameEntity createGameEntityFromDTO(GameUpdateDTO dto) {
        DeveloperEntity developerEntity = developerService.findDeveloperByNameOrThrow(dto.getDeveloperName());
        GameEntity gameEntity = gameMapper.toGameEntity(dto);
        gameEntity.setDeveloper(developerEntity);
        return gameEntity;
    }

    private boolean isValid(GameUpdateDTO dto) {
        return validator.validate(dto).isEmpty();
    }

    private List<GameUpdateDTO> convertFileToDTOList(MultipartFile file) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(file.getInputStream())) {
            return objectMapper.readerForListOf(GameUpdateDTO.class)
                    .readValue(bufferedInputStream);
        } catch (IOException e) {
            throw new FileParsingException(e.getMessage());
        }
    }

    private void updateValues(GameUpdateDTO dto, GameEntity gameEntity) {
        DeveloperEntity developerEntity = developerService.findDeveloperByNameOrThrow(dto.getDeveloperName());
        gameEntity.setDeveloper(developerEntity);
        gameEntity.setTitle(dto.getTitle());
        gameEntity.setGenre(dto.getGenre());
        gameEntity.setYearReleased(dto.getYearReleased());
    }

    private GameEntity getGameByIdOrThrow(Long id) {
        return gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException("Game with id %d not found".formatted(id)));
    }
}
