package dev.profitsoft.videogames.service;

import dev.profitsoft.videogames.dto.developer.DeveloperDTO;
import dev.profitsoft.videogames.entity.DeveloperEntity;
import dev.profitsoft.videogames.exception.DeveloperNotFoundException;
import dev.profitsoft.videogames.exception.UniqueValueViolationException;
import dev.profitsoft.videogames.mapper.DeveloperMapper;
import dev.profitsoft.videogames.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final DeveloperMapper developerMapper;

    public DeveloperEntity findDeveloperByNameOrThrow(String name) {
        return developerRepository.findByName(name)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer with name %s not found".formatted(name)));
    }

    public List<DeveloperDTO> getAllDevelopers() {
        return developerRepository.findAll().stream().map(developerMapper::toDeveloperDTO).toList();
    }

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

    public void deleteDeveloperById(Long id) {
        if (!developerRepository.existsById(id)) {
            throw new DeveloperNotFoundException("Developer with id %d not found".formatted(id));
        }
        developerRepository.deleteById(id);
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
