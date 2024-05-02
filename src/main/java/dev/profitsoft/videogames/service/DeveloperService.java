package dev.profitsoft.videogames.service;

import dev.profitsoft.videogames.dto.developer.DeveloperDTO;
import dev.profitsoft.videogames.entity.DeveloperEntity;
import dev.profitsoft.videogames.exception.exceptions.DeveloperNotFoundException;
import dev.profitsoft.videogames.exception.exceptions.UniqueValueViolationException;
import dev.profitsoft.videogames.mapper.DeveloperMapper;
import dev.profitsoft.videogames.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing developer-related operations.
 */
@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final DeveloperMapper developerMapper;


    /**
     * Retrieves a list of all developers as DeveloperDTOs.
     *
     * @return A list of DeveloperDTOs representing all developers
     */
    public List<DeveloperDTO> getAllDevelopers() {
        return developerRepository.findAll().stream().map(developerMapper::toDeveloperDTO).toList();
    }

    /**
     * Adds a new developer to the repository.
     *
     * @param developerDTO The DeveloperDTO representing the new developer
     * @return The DeveloperDTO representing the added developer
     * @throws UniqueValueViolationException If a developer with the same name already exists
     */
    public DeveloperDTO addDeveloper(DeveloperDTO developerDTO) {
        DeveloperEntity entity = developerMapper.toDeveloperEntity(developerDTO);
        String name = entity.getName();
        try {
            developerRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueValueViolationException("Developer with name %s already exists".formatted(name));
        }
        return developerMapper.toDeveloperDTO(entity);
    }

    /**
     * Updates an existing developer by ID.
     *
     * @param id           The ID of the developer to update
     * @param developerDTO The DeveloperDTO containing the updated values
     * @throws DeveloperNotFoundException    If no developer with the given ID is found
     * @throws UniqueValueViolationException If a developer with the same name already exists
     */
    public void updateDeveloperById(Long id, DeveloperDTO developerDTO) {
        DeveloperEntity entity = getDeveloperByIdOrThrow(id);
        updateValues(developerDTO, entity);
        String name = entity.getName();
        try {
            developerRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueValueViolationException("Developer with name %s already exists".formatted(name));
        }
    }

    /**
     * Deletes a developer by ID.
     *
     * @param id The ID of the developer to delete
     * @throws DeveloperNotFoundException If no developer with the given ID is found
     */
    public void deleteDeveloperById(Long id) {
        if (!developerRepository.existsById(id)) {
            throw new DeveloperNotFoundException("Developer with id %d not found".formatted(id));
        }
        developerRepository.deleteById(id);
    }

    /**
     * Finds a DeveloperEntity by name.
     *
     * @param name The name of the developer to find
     * @return The DeveloperEntity with the given name
     * @throws DeveloperNotFoundException If no developer with the given name is found
     */
    public DeveloperEntity findDeveloperByNameOrThrow(String name) {
        return developerRepository.findByName(name)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer with name %s not found".formatted(name)));
    }

    private void updateValues(DeveloperDTO dto, DeveloperEntity entity) {
        entity.setLocation(dto.getLocation());
        entity.setName(dto.getName());
        entity.setYearFounded(dto.getYearFounded());
        entity.setNumberOfEmployees(dto.getNumberOfEmployees());
    }

    private DeveloperEntity getDeveloperByIdOrThrow(Long id) {
        return developerRepository.findById(id).orElseThrow(
                () -> new DeveloperNotFoundException("Developer with id %s not found".formatted(id)));
    }
}
