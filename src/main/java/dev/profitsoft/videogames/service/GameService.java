package dev.profitsoft.videogames.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.profitsoft.videogames.dto.game.*;
import dev.profitsoft.videogames.entity.DeveloperEntity;
import dev.profitsoft.videogames.entity.GameEntity;
import dev.profitsoft.videogames.exception.FileParsingException;
import dev.profitsoft.videogames.exception.NotFoundException;
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
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final DeveloperService developerService;
    private final GameMapper gameMapper;
    private final ObjectMapper objectMapper;
    private final Validator validator;


    public GameUpdateDTO saveGame(GameUpdateDTO dto) {
        GameEntity gameEntity = createGameEntityFromDTO(dto);
        GameEntity savedGame = gameRepository.save(gameEntity);
        return gameMapper.toGameUpdateDTO(savedGame);
    }

    public GameDTO getGame(Long id) {
        GameEntity gameEntity = getByIdOrThrow(id);
        return gameMapper.toGameDTO(gameEntity);
    }

    public void updateGame(Long id, GameUpdateDTO dto) {
        GameEntity gameEntity = getByIdOrThrow(id);
        updateValues(dto, gameEntity);
        gameRepository.save(gameEntity);
    }

    public void deleteGame(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new NotFoundException("Game with id %d not found".formatted(id));
        }

        gameRepository.deleteById(id);
    }

    public GameListDTO searchGamesWithFilters(GameSearchDTO dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage() - 1, dto.getSize());

        Page<GameEntity> gamesPage = gameRepository.findGamesWithFilters(dto.getDeveloperId(), dto.getYearReleased(), pageRequest);

        List<GameInfoDTO> gameInfoDTOList = gamesPage.map(gameMapper::toGameInfoDTO).toList();

        return GameListDTO.builder().games(gameInfoDTOList).totalPages(gamesPage.getTotalPages()).build();
    }

    public void generateReport(GameSearchDTO dto, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=games.csv");

        try {
            StringBuilder sb = new StringBuilder();
            List<GameEntity> list = gameRepository.findAllForReport(dto.getDeveloperId(), dto.getYearReleased());
            sb.append("Title;Genre\n");
            for (GameEntity gameEntity : list) {
                sb.append(gameEntity.getTitle());
                sb.append(";");
                sb.append(gameEntity.getGenre());
                sb.append("\n");
            }
            response.getOutputStream().write(sb.toString().getBytes());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new FileParsingException(e.getMessage());
        }
    }

    public GameUploadDTO uploadFromFile(MultipartFile file) {
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
            } catch (NotFoundException e) {
                invalidDto++;
            }
        }

        gameRepository.saveAll(gamesToSave);
        return new GameUploadDTO(gamesToSave.size(), invalidDto);
    }

    private GameEntity createGameEntityFromDTO(GameUpdateDTO dto) {
        GameEntity gameEntity = gameMapper.toEntity(dto);
        DeveloperEntity developerEntity = developerService.findDeveloperByNameOrThrow(dto.getDeveloperName());
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

    private GameEntity getByIdOrThrow(Long id) {
        return gameRepository.findById(id).orElseThrow(() -> new NotFoundException("Game with id %d not found".formatted(id)));
    }

}
