package dev.profitsoft.videogames.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.profitsoft.videogames.VideoGamesApplication;
import dev.profitsoft.videogames.dto.developer.DeveloperDTO;
import dev.profitsoft.videogames.dto.response.RestResponse;
import dev.profitsoft.videogames.entity.DeveloperEntity;
import dev.profitsoft.videogames.mapper.DeveloperMapper;
import dev.profitsoft.videogames.repository.DeveloperRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = VideoGamesApplication.class)
@AutoConfigureMockMvc
@Transactional
class DeveloperControllerIntTest {
    private static final String DEVELOPER_NAME = "Test Developer";
    private static final String LOCATION = "San Francisco";
    private static final int YEAR_FOUNDED = 1986;
    private static final int EMPLOYEES = 18000;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeveloperMapper developerMapper;

    private DeveloperEntity savedDeveloper;

    @BeforeEach
    void setUp() {
        DeveloperEntity developerEntity = new DeveloperEntity();
        developerEntity.setName(DEVELOPER_NAME);
        developerEntity.setLocation(LOCATION);
        developerEntity.setYearFounded(YEAR_FOUNDED);
        developerEntity.setNumberOfEmployees(EMPLOYEES);
        savedDeveloper = developerRepository.save(developerEntity);
    }

    @Test
    void getDevelopers_ValidInput_Success() throws Exception {
        List<DeveloperDTO> developers = developerRepository.findAll().stream()
                .map(developerMapper::toDeveloperDTO)
                .toList();

        String requestBody = objectMapper.writeValueAsString(developers);

        mvc.perform(get("/api/developer"))
                .andExpectAll(
                        status().isOk(),
                        content().json(requestBody)
                );
    }

    @Test
    void addDeveloper_ValidInput_Success() throws Exception {
        String newDeveloperName = "New Developer";
        String requestBody = """
                {
                    "name": "%s",
                    "location": "%s",
                    "yearFounded": %d,
                    "numberOfEmployees": %d
                }
                """.formatted(newDeveloperName, LOCATION, YEAR_FOUNDED, EMPLOYEES);

        mvc.perform(post("/api/developer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name").value(newDeveloperName),
                        jsonPath("$.yearFounded").value(YEAR_FOUNDED)
                );
    }

    @Test
    void addDeveloper_EmptyName_ExceptionThrown() throws Exception {
        String emptyName = "";
        String requestBody = """
                {
                    "name": %s,
                    "location": %s,
                    "yearFounded": %d,
                    "numberOfEmployees": %d
                }
                """.formatted(emptyName, LOCATION, YEAR_FOUNDED, EMPLOYEES);

        mvc.perform(post("/api/developer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDeveloper_ExistentName_ExceptionThrown() throws Exception {
        String requestBody = """
                {
                    "name": "%s",
                    "location": "%s",
                    "yearFounded": %d,
                    "numberOfEmployees": %d
                }
                """.formatted(DEVELOPER_NAME, LOCATION, YEAR_FOUNDED, EMPLOYEES);

        mvc.perform(post("/api/developer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void updateDeveloper_ValidInput_Success() throws Exception {
        Long id = savedDeveloper.getId();
        String updatedName = "New Name";
        int updatedYear = 1995;
        String requestBody = """
                {
                    "name": "%s",
                    "location": "%s",
                    "yearFounded": %d,
                    "numberOfEmployees": %d
                }
                """.formatted(updatedName, LOCATION, updatedYear, EMPLOYEES);
        String responseBody = """
                {
                    "response": "Developer updated successfully"
                }
                """;
        RestResponse restResponse = objectMapper.readValue(responseBody, RestResponse.class);

        mvc.perform(put("/api/developer/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(restResponse.getResponse()));
    }

    @Test
    void updateDeveloper_NonExistentId_ExceptionThrown() throws Exception {
        Long nonExistentId = 999L;
        String updatedName = "New Name";
        int updatedYear = 1995;
        String requestBody = """
                {
                    "name": "%s",
                    "location": "%s",
                    "yearFounded": %d,
                    "numberOfEmployees": %d
                }
                """.formatted(updatedName, LOCATION, updatedYear, EMPLOYEES);

        mvc.perform(put("/api/developer/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDeveloper_ValidInput_Success() throws Exception {
        Long id = savedDeveloper.getId();
        String responseBody = """
                {
                    "response": "Developer deleted"
                }
                """;
        RestResponse restResponse = objectMapper.readValue(responseBody, RestResponse.class);

        mvc.perform(delete("/api/developer/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(restResponse.getResponse()));
    }

    @Test
    void deleteDeveloper_NonExistentId_ExceptionThrown() throws Exception {
        Long nonExistentId = 999L;

        mvc.perform(delete("/api/developer/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}