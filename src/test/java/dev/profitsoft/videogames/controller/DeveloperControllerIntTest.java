package dev.profitsoft.videogames.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.profitsoft.videogames.VideoGamesApplication;
import dev.profitsoft.videogames.dto.developer.DeveloperDTO;
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
    void testGetDevelopers() throws Exception {
        List<DeveloperDTO> developers = developerRepository.findAll().stream()
                .map(developerMapper::toDeveloperDTO)
                .toList();

        String json = objectMapper.writeValueAsString(developers);

        mvc.perform(get("/api/developer"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void testAddDeveloper() throws Exception {
        String requestBody = """
                {
                    "name": "New Developer",
                    "location": "New York",
                    "yearFounded": 2010,
                    "numberOfEmployees": 500
                }
                """;

        mvc.perform(post("/api/developer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Developer"))
                .andExpect(jsonPath("$.yearFounded").value(2010));
    }

    @Test
    void testAddDeveloperEmptyName() throws Exception {
        String requestBody = """
                {
                    "name": "",
                    "location": "New York",
                    "yearFounded": 2010,
                    "numberOfEmployees": 500
                }
                """;

        mvc.perform(post("/api/developer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddDeveloperExistentName() throws Exception {
        String requestBody = """
                {
                    "name": "Ubisoft",
                    "location": "New York",
                    "yearFounded": 2010,
                    "numberOfEmployees": 500
                }
                """;

        mvc.perform(post("/api/developer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateDeveloper() throws Exception {
        Long id = savedDeveloper.getId();
        String requestBody = """
                {
                    "name": "Updated Ubisoft",
                    "location": "Los Angeles",
                    "yearFounded": 1990,
                    "numberOfEmployees": 20000
                }
                """;

        mvc.perform(put("/api/developer/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Developer updated successfully"));
    }

    @Test
    void testUpdateDeveloperNotFound() throws Exception {
        Long nonExistentId = 999L;
        String requestBody = """
                {
                    "name": "Updated Ubisoft",
                    "location": "Los Angeles",
                    "yearFounded": 1990,
                    "numberOfEmployees": 20000
                }
                """;

        mvc.perform(put("/api/developer/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateDeveloperExistentName() throws Exception {
        Long id = savedDeveloper.getId();
        String requestBody = """
            {
                "name": "Ubisoft",
                "location": "Los Angeles",
                "yearFounded": 1990,
                "numberOfEmployees": 20000
            }
            """;

        mvc.perform(put("/api/developer/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void testDeleteDeveloper() throws Exception {
        Long id = savedDeveloper.getId();

        mvc.perform(delete("/api/developer/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Developer deleted successfully"));
    }

    @Test
    void testDeleteDeveloperNotFound() throws Exception {
        Long nonExistentId = 999L;

        mvc.perform(delete("/api/developer/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}