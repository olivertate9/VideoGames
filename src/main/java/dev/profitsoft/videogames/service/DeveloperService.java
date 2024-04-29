package dev.profitsoft.videogames.service;

import dev.profitsoft.videogames.dto.developer.DeveloperDTO;
import dev.profitsoft.videogames.entity.DeveloperEntity;
import dev.profitsoft.videogames.exception.NotFoundException;
import dev.profitsoft.videogames.exception.UniqueValueException;
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

    public DeveloperEntity findDeveloperByNameOrThrow(String developerName) {
        return developerRepository.findByName(developerName)
                .orElseThrow(() -> new NotFoundException("Developer with name %s not found".formatted(developerName)));
    }

    public List<DeveloperDTO> getAllDevelopers() {
        return developerRepository.findAll().stream().map(developerMapper::toDeveloperDTO).toList();
    }

    public DeveloperDTO addDeveloper(DeveloperDTO developerDTO) {
        DeveloperEntity entity = developerMapper.toDeveloperEntity(developerDTO);
        try {
            developerRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueValueException("Developer with name %s already exists".formatted(entity.getName()));
        }
        return developerMapper.toDeveloperDTO(entity);
    }

    public void updateDeveloperById(Long id, DeveloperDTO developerDTO) {
        DeveloperEntity entity = getDeveloperByIdOrThrow(id);
        updateValues(developerDTO, entity);
        try {
            developerRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueValueException("Developer with name %s already exists".formatted(entity.getName()));
        }
    }

    public void deleteDeveloperById(Long id) {
        if (!developerRepository.existsById(id)) {
            throw new NotFoundException("Developer with id %d not found".formatted(id));
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
        return developerRepository.findById(id).orElseThrow(() -> new NotFoundException("Developer with id %s not found".formatted(id)));
    }
}
