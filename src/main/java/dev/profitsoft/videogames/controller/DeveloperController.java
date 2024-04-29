package dev.profitsoft.videogames.controller;

import dev.profitsoft.videogames.dto.developerDTOs.DeveloperDTO;
import dev.profitsoft.videogames.dto.responseDTOs.RestResponse;
import dev.profitsoft.videogames.service.DeveloperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/developer")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    @GetMapping
    public ResponseEntity<List<DeveloperDTO>> getDevelopers() {
        return ResponseEntity.ok(developerService.getAllDevelopers());
    }

    @PostMapping
    public ResponseEntity<DeveloperDTO> addDeveloper(@Valid @RequestBody DeveloperDTO developerDTO) {
        return ResponseEntity.ok(developerService.addDeveloper(developerDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestResponse> updateDeveloper(@PathVariable Long id, @Valid @RequestBody DeveloperDTO developerDTO) {
        developerService.updateDeveloperById(id, developerDTO);
        return ResponseEntity.ok(new RestResponse("Developer updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse> deleteDeveloper(@PathVariable Long id) {
        developerService.deleteDeveloperById(id);
        return ResponseEntity.ok(new RestResponse("Developer deleted successfully"));
    }
}
