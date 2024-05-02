package dev.profitsoft.videogames.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.profitsoft.videogames.VideoGamesApplication;
import dev.profitsoft.videogames.dto.game.GameListDTO;
import dev.profitsoft.videogames.dto.game.GameUploadDTO;
import dev.profitsoft.videogames.dto.response.RestResponse;
import dev.profitsoft.videogames.entity.DeveloperEntity;
import dev.profitsoft.videogames.entity.GameEntity;
import dev.profitsoft.videogames.mapper.GameMapper;
import dev.profitsoft.videogames.repository.GameRepository;
import dev.profitsoft.videogames.service.DeveloperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = VideoGamesApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GameControllerIntTest {

    private static final String DEVELOPER_NAME = "Ubisoft";
    private static final String TITLE = "Test Title";
    private static final int YEAR_RELEASED = 1999;
    private static final String GENRE = "Action";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:alpine");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private DeveloperService developerService;

    private GameEntity savedGame;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
        GameEntity gameEntity = new GameEntity();
        DeveloperEntity developerEntity = developerService.findDeveloperByNameOrThrow(DEVELOPER_NAME);
        gameEntity.setDeveloper(developerEntity);
        gameEntity.setTitle(TITLE);
        gameEntity.setYearReleased(YEAR_RELEASED);
        gameEntity.setGenre(GENRE);
        savedGame = gameRepository.save(gameEntity);
    }

    @Test
    void addGame_ValidInput_Success() throws Exception {
        String requestBody = """
                {
                    "title": "%s",
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                }
                """.formatted(TITLE, DEVELOPER_NAME, YEAR_RELEASED, GENRE);

        mvc.perform(post("/api/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isCreated(),
                        content().json(requestBody),
                        jsonPath("$.title").value(TITLE),
                        jsonPath("$.developerName").value(DEVELOPER_NAME)
                );
    }

    @Test
    void addGame_NoTitle_ExceptionThrown() throws Exception {
        String requestBody = """
                {
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                }
                """.formatted(DEVELOPER_NAME, YEAR_RELEASED, GENRE);

        mvc.perform(post("/api/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addGame_InvalidYear_ExceptionThrown() throws Exception {
        int invalidYear = 2030;
        String requestBody = """
                {
                    "title": "%s",
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                }
                """.formatted(TITLE, DEVELOPER_NAME, invalidYear, GENRE);

        mvc.perform(post("/api/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getGame_ValidInput_Success() throws Exception {
        String requestBody = objectMapper.writeValueAsString(gameMapper.toGameDTO(savedGame));
        Long id = savedGame.getId();

        mvc.perform(get("/api/game/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        content().json(requestBody),
                        jsonPath("$.title").value(savedGame.getTitle()),
                        jsonPath("$.yearReleased").value(savedGame.getYearReleased())
                );
    }

    @Test
    void getGame_NonExistentId_ExceptionThrown() throws Exception {
        final Long nonExistentId = 999L;

        mvc.perform(get("/api/game/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void updateGame_ValidInput_Success() throws Exception {
        Long id = savedGame.getId();
        String updatedTitle = "New Title";
        int updatedYear = 2008;
        String responseBody = """
                {
                    "response": "Game updated successfully"
                }
                """;
        String requestBody = """
                {
                    "title": "%s",
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                }
                """.formatted(updatedTitle, DEVELOPER_NAME, updatedYear, GENRE);

        RestResponse restResponse = objectMapper.readValue(responseBody, RestResponse.class);

        mvc.perform(put("/api/game/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isOk(),
                        content().json(responseBody),
                        jsonPath("$.response").value(restResponse.getResponse())
                );
    }

    @Test
    void updateGame_NoTitle_ExceptionThrown() throws Exception {
        Long id = savedGame.getId();
        String requestBody = """
                {
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                }
                """.formatted(DEVELOPER_NAME, YEAR_RELEASED, GENRE);

        mvc.perform(put("/api/game/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateGame_NonExistentId_ExceptionThrown() throws Exception {
        Long nonExistentId = 999L;
        String newTitle = "New Game";
        String requestBody = """
                {
                    "title": "%s",
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                }
                """.formatted(newTitle, DEVELOPER_NAME, YEAR_RELEASED, GENRE);

        mvc.perform(put("/api/game/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGame_ValidInput_Success() throws Exception {
        Long id = savedGame.getId();
        String responseBody = """
                {
                    "response": "Game deleted"
                }
                """;

        RestResponse restResponse = objectMapper.readValue(responseBody, RestResponse.class);

        mvc.perform(delete("/api/game/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        content().json(responseBody),
                        jsonPath("$.response").value(restResponse.getResponse())
                );
    }

    @Test
    void deleteGame_NonExistentId_ExceptionThrown() throws Exception {
        final Long nonExistentId = 999L;

        mvc.perform(delete("/api/game/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void findGamesWithFilters_ValidInput_Success() throws Exception {
        int page = 1;
        int size = 1;
        int totalPages = 1;
        Long developerId = 3L;

        String requestBody = """
                {
                    "yearReleased": %d,
                    "developerId": "%d",
                    "page": %d,
                    "size": %d
                }
                """.formatted(YEAR_RELEASED, developerId, page, size);

        GameListDTO listDTO = new GameListDTO(List.of(gameMapper.toGameInfoDTO(savedGame)), totalPages);

        mvc.perform(post("/api/game/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(listDTO)),
                        jsonPath("$.totalPages").value(listDTO.getTotalPages())
                );
    }

    @Test
    void findGamesWithFilters_NonExistentDeveloperId_EmptyResponse() throws Exception {
        gameRepository.deleteAll();
        Long nonExistentId = 999L;
        int page = 1;
        int size = 1;
        int totalPages = 0;
        String requestBody = """
                {
                    "yearReleased": %d,
                    "developerId": "%d",
                    "page": %d,
                    "size": %d
                }
                """.formatted(YEAR_RELEASED, nonExistentId, page, size);

        GameListDTO listDTO = new GameListDTO(List.of(), totalPages);

        mvc.perform(post("/api/game/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(listDTO)),
                        jsonPath("$.totalPages").value(listDTO.getTotalPages())
                );
    }

    @Test
    void generateReport_ValidInput_Success() throws Exception {
        Long developerId = 3L;
        String requestBody = """
                {
                    "yearReleased": %d,
                    "developerId": "%d"
                }
                """.formatted(YEAR_RELEASED, developerId);
        String expected = """
                Title;Genre
                %s;%s
                """.formatted(savedGame.getTitle(), savedGame.getGenre());
        String headers = "attachment; filename=games.csv";

        mvc.perform(post("/api/game/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_OCTET_STREAM),
                        header().stringValues(HttpHeaders.CONTENT_DISPOSITION, headers),
                        content().bytes(expected.getBytes())
                );
    }

    @Test
    void generateReport_NonExistentDeveloper_EmptyReport() throws Exception {
        gameRepository.deleteAll();
        Long nonExistentId = 999L;
        String requestBody = """
                {
                    "yearReleased": %d,
                    "developerId": "%d"
                }
                """.formatted(YEAR_RELEASED, nonExistentId);
        String expected = "Title;Genre\n";
        String headers = "attachment; filename=games.csv";

        mvc.perform(post("/api/game/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_OCTET_STREAM),
                        header().stringValues(HttpHeaders.CONTENT_DISPOSITION, headers),
                        content().bytes(expected.getBytes())
                );
    }

    @ParameterizedTest
    @CsvSource(delimiterString = ";", value = {
            "The Legend of Zelda: Breath of the Wild;Nintendo;2017;Action, Adventure",
            "Super Mario Odyssey;Nintendo;2017;Platformer"
    })
    void uploadFromFile_ValidInputs_Success(String title, String developerName, int yearReleased, String genre) throws Exception {
        String json = """
                [
                  {
                    "title": "%s",
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                  }
                ]
                """.formatted(title, developerName, yearReleased, genre);
        String fileName = "file";
        int successUploads = 1;
        int failUploads = 0;

        MockMultipartFile file = new MockMultipartFile(fileName, json.getBytes(StandardCharsets.UTF_8));
        GameUploadDTO uploadDTO = new GameUploadDTO(successUploads, failUploads);

        mvc.perform(multipart("/api/game/upload")
                        .file(file))
                .andExpectAll(
                        status().isCreated(),
                        content().json(objectMapper.writeValueAsString(uploadDTO)),
                        jsonPath("$.successUploads").value(uploadDTO.getSuccessUploads()),
                        jsonPath("$.failUploads").value(uploadDTO.getFailUploads())
                );
    }

    @Test
    void uploadFromFile_EmptyFile_ExceptionThrown() throws Exception {
        String fileName = "file";
        String empty = "";
        MockMultipartFile file = new MockMultipartFile(fileName, empty.getBytes(StandardCharsets.UTF_8));

        mvc.perform(multipart("/api/game/upload")
                        .file(file))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void uploadFromFile_InvalidInput_ZeroUploads() throws Exception {
        int invalidYear = 2030;
        String json = """
                [
                  {
                    "title": "%s",
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                  }
                ]
                """.formatted(TITLE, DEVELOPER_NAME, invalidYear, GENRE);
        String fileName = "file";
        int successUploads = 0;
        int failUploads = 1;

        MockMultipartFile file = new MockMultipartFile(fileName, json.getBytes(StandardCharsets.UTF_8));
        GameUploadDTO uploadDTO = new GameUploadDTO(successUploads, failUploads);

        mvc.perform(multipart("/api/game/upload")
                        .file(file))
                .andExpectAll(
                        status().isCreated(),
                        content().json(objectMapper.writeValueAsString(uploadDTO)),
                        jsonPath("$.successUploads").value(uploadDTO.getSuccessUploads()),
                        jsonPath("$.failUploads").value(uploadDTO.getFailUploads())
                );
    }
}

