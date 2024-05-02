package dev.profitsoft.videogames.controller;

import dev.profitsoft.videogames.dto.developer.DeveloperDTO;
import dev.profitsoft.videogames.dto.response.RestResponse;
import dev.profitsoft.videogames.service.DeveloperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing developers.
 */
@Tag(name = "Developer", description = "Developer management APIs")
@RestController
@RequestMapping("api/developer")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    /**
     * Endpoint to retrieve a list of all developers.
     *
     * @return A ResponseEntity containing a list of DeveloperDTO objects with HTTP status 200 (OK).
     */
    @Operation(
            summary = "Retrieve a list of all developers",
            description = "Returns a list of all developers in the repository.")
    @GetMapping
    public ResponseEntity<List<DeveloperDTO>> getDevelopers() {
        return ResponseEntity.ok(developerService.getAllDevelopers());
    }

    /**
     * Endpoint to add a new developer to the repository.
     *
     * @param developerDTO The DeveloperDTO object representing the new developer
     * @return A ResponseEntity containing the newly added DeveloperDTO object with HTTP status 201 (CREATED).
     */
    @Operation(
            summary = "Add a new developer",
            description = "Adds a new developer to the system."
    )
    @ApiResponse(responseCode = "201", description = "Developer added successfully")
    @PostMapping
    public ResponseEntity<DeveloperDTO> addDeveloper(@Valid @RequestBody DeveloperDTO developerDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(developerService.addDeveloper(developerDTO));
    }

    /**
     * Endpoint to update an existing developer by ID.
     *
     * @param id           The ID of the developer to update
     * @param developerDTO The DeveloperDTO object containing the updated details
     * @return A ResponseEntity with a success message and HTTP status 200 (OK).
     */
    @Operation(
            summary = "Update an existing developer",
            description = "Updates details of an existing developer based on the provided ID."
    )
    @ApiResponse(responseCode = "200", description = "Developer updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse> updateDeveloper(
            @PathVariable Long id,
            @Valid @RequestBody DeveloperDTO developerDTO
    ) {
        developerService.updateDeveloperById(id, developerDTO);
        return ResponseEntity.ok(new RestResponse("Developer updated successfully"));
    }

    /**
     * Endpoint to delete a developer by ID.
     *
     * @param id The ID of the developer to delete
     * @return A ResponseEntity with a success message and HTTP status 200 (OK).
     */
    @Operation(
            summary = "Delete a developer",
            description = "Deletes a developer based on the provided ID."
    )
    @ApiResponse(responseCode = "200", description = "Developer deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse> deleteDeveloper(@PathVariable Long id) {
        developerService.deleteDeveloperById(id);
        return ResponseEntity.ok(new RestResponse("Developer deleted"));
    }
}
