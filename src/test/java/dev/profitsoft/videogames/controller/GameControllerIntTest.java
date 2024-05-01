package dev.profitsoft.videogames.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.profitsoft.videogames.VideoGamesApplication;
import dev.profitsoft.videogames.dto.game.GameDTO;
import dev.profitsoft.videogames.dto.game.GameInfoDTO;
import dev.profitsoft.videogames.dto.game.GameListDTO;
import dev.profitsoft.videogames.dto.game.GameUploadDTO;
import dev.profitsoft.videogames.dto.response.ErrorResponse;
import dev.profitsoft.videogames.dto.response.RestResponse;
import dev.profitsoft.videogames.entity.DeveloperEntity;
import dev.profitsoft.videogames.entity.GameEntity;
import dev.profitsoft.videogames.mapper.GameMapper;
import dev.profitsoft.videogames.repository.GameRepository;
import dev.profitsoft.videogames.service.DeveloperService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = VideoGamesApplication.class)
@AutoConfigureMockMvc
@Transactional
class GameControllerIntTest {

    private static final String DEVELOPER_NAME = "Ubisoft";
    private static final int YEAR_RELEASED = 1999;
    private static final String GENRE = "Action";

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
        GameEntity gameEntity = new GameEntity();
        DeveloperEntity developerEntity = developerService.findDeveloperByNameOrThrow(DEVELOPER_NAME);
        gameEntity.setDeveloper(developerEntity);
        gameEntity.setTitle("Test Title");
        gameEntity.setYearReleased(YEAR_RELEASED);
        gameEntity.setGenre(GENRE);
        savedGame = gameRepository.save(gameEntity);
    }

    @Test
    void testAddGame() throws Exception {
        String title = "Add Game Test Title";
        String requestBody = """
                {
                    "title": "%s",
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                }
                """.formatted(title, DEVELOPER_NAME, YEAR_RELEASED, GENRE);

        mvc.perform(post("/api/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(requestBody))
                .andReturn();

        GameEntity entity = gameRepository.findByTitle(title);
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo(title);
        assertThat(entity.getDeveloper().getName()).isEqualTo(DEVELOPER_NAME);
        assertThat(entity.getGenre()).isEqualTo(GENRE);
    }

    @Test
    void testAddGameValidateRequiredFields() throws Exception {
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
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testAddGameValidateInvalidData() throws Exception {
        String requestBody = """
                {
                    "title": "Test Title",
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "InvalidGenre"
                }
                """.formatted(DEVELOPER_NAME, 2030);

        mvc.perform(post("/api/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testGetGame() throws Exception {
        String json = objectMapper.writeValueAsString(gameMapper.toGameDTO(savedGame));
        Long id = savedGame.getId();

        MvcResult result = mvc.perform(get("/api/game/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(json))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        GameDTO gameDTO = objectMapper.readValue(content, GameDTO.class);

        assertThat(gameDTO).isNotNull();
        assertThat(gameDTO.getTitle()).isEqualTo(savedGame.getTitle());
        assertThat(gameDTO.getYearReleased()).isEqualTo(savedGame.getYearReleased());
    }

    @Test
    void testGetGameWithNonExistentId() throws Exception {
        Long nonExistentId = 999L;

        mvc.perform(get("/api/game/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void testUpdateGame() throws Exception {
        Long id = savedGame.getId();
        String updatedTitle = "Updated Test Title";
        String requestBody = """
                {
                    "title": "%s",
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                }
                """.formatted(updatedTitle, DEVELOPER_NAME, YEAR_RELEASED, GENRE);

        MvcResult result = mvc.perform(put("/api/game/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        RestResponse restResponse = objectMapper.readValue(content, RestResponse.class);

        assertThat(restResponse).isNotNull();
        assertThat(restResponse.getResponse()).isEqualTo("Game updated successfully");
    }

    @Test
    void testUpdateGameMissingValues() throws Exception {
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
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testUpdateGameNonExistentId() throws Exception {
        Long nonExistentId = 999L;
        String requestBody = """
                {
                    "title": "Test Title",
                    "developerName": "%s",
                    "yearReleased": %d,
                    "genre": "%s"
                }
                """.formatted(DEVELOPER_NAME, YEAR_RELEASED, GENRE);

        mvc.perform(put("/api/game/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void testDeleteGame() throws Exception {
        Long id = savedGame.getId();

        MvcResult result = mvc.perform(delete("/api/game/{id}", id))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        RestResponse restResponse = objectMapper.readValue(content, RestResponse.class);

        assertThat(restResponse).isNotNull();
        assertThat(restResponse.getResponse()).isEqualTo("Game deleted");
    }

    @Test
    void testDeleteGameNonExistentId() throws Exception {
        Long nonExistentId = 999L;

        mvc.perform(delete("/api/game/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void testFindGamesWithFilters() throws Exception {
        String requestBody = """
                {
                    "yearReleased": %d,
                    "developerName": "%s",
                    "page": 1,
                    "size": 1
                }
                """.formatted(YEAR_RELEASED, DEVELOPER_NAME);

        MvcResult result = mvc.perform(post("/api/game/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        GameListDTO listDTO = objectMapper.readValue(content, GameListDTO.class);
        GameInfoDTO infoDTO = gameMapper.toGameInfoDTO(savedGame);

        assertThat(listDTO).isNotNull();
        assertThat(listDTO.getGames().get(0).getTitle()).isEqualTo(infoDTO.getTitle());
        assertThat(listDTO.getTotalPages()).isEqualTo(1);
    }

    @Test
    void testFindGamesListEmpty() throws Exception {
        gameRepository.deleteAll();
        String requestBody = """
                {
                    "yearReleased": %d,
                    "developerName": "NonExistentDeveloper",
                    "page": 1,
                    "size": 10
                }
                """.formatted(YEAR_RELEASED);

        MvcResult result = mvc.perform(post("/api/game/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        GameListDTO listDTO = objectMapper.readValue(content, GameListDTO.class);

        assertThat(listDTO.getGames()).size().isZero();
    }

    @Test
    void testGenerateReport() throws Exception {
        String requestBody = """
                {
                    "yearReleased": %d,
                    "developerName": "%s"
                }
                """.formatted(YEAR_RELEASED, DEVELOPER_NAME);

        MvcResult result = mvc.perform(post("/api/game/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String header = result.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
        String contentType = result.getResponse().getContentType();
        byte[] content = result.getResponse().getContentAsByteArray();
        String expected = """
                Title;Genre
                %s;%s
                """.formatted(savedGame.getTitle(), savedGame.getGenre());

        assertThat(contentType).isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        assertThat(header).contains("attachment; filename=games.csv");
        assertThat(content).isEqualTo(expected.getBytes());

    }

    @Test
    void testGenerateReportIsEmpty() throws Exception {
        gameRepository.deleteAll();

        String requestBody = """
                {
                    "yearReleased": %d,
                    "developerName": "NonExistentDeveloper"
                }
                """.formatted(YEAR_RELEASED);
        String defaultResponse = "Title;Genre\n";
        MvcResult result = mvc.perform(post("/api/game/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        byte[] content = result.getResponse().getContentAsByteArray();
        assertThat(content).isEqualTo(defaultResponse.getBytes());
    }

    @ParameterizedTest
    @CsvSource(delimiterString = ";", value = {
            "The Legend of Zelda: Breath of the Wild;Nintendo;2017;Action, Adventure",
            "Super Mario Odyssey;Nintendo;2017;Platformer"
    })
    void testUploadFromFile(String title, String developerName, int yearReleased, String genre) throws Exception {
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
        MockMultipartFile file = new MockMultipartFile("file", json.getBytes());

        MvcResult result = mvc.perform(multipart("/api/game/upload")
                        .file(file))
                .andExpect(status().isCreated())
                .andReturn();

        GameUploadDTO uploadDTO = objectMapper.readValue(result.getResponse().getContentAsString(), GameUploadDTO.class);

        assertThat(uploadDTO).isNotNull();
        assertThat(uploadDTO.getFailUploads()).isZero();
        assertThat(uploadDTO.getSuccessUploads()).isEqualTo(1);
    }

    @Test
    void testUploadEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "".getBytes());

        MvcResult result = mvc.perform(multipart("/api/game/upload")
                        .file(file))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        ErrorResponse error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(error.getDescription()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());
    }

    @Test
    void testUploadInvalidFields() throws Exception {
        String json = """
                [
                  {
                    "title": "Test Game",
                    "developerName": "Developer",
                    "yearReleased": 2030,
                    "genre": "InvalidGenre"
                  }
                ]
                """;
        MockMultipartFile file = new MockMultipartFile("file", json.getBytes());

        MvcResult result = mvc.perform(multipart("/api/game/upload")
                        .file(file))
                .andExpect(status().isCreated())
                .andReturn();

        GameUploadDTO uploadDTO = objectMapper.readValue(result.getResponse().getContentAsString(), GameUploadDTO.class);

        assertThat(uploadDTO).isNotNull();
        assertThat(uploadDTO.getFailUploads()).isEqualTo(1);
        assertThat(uploadDTO.getSuccessUploads()).isZero();
    }
}

