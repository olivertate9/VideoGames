package dev.profitsoft.videogames.mapper;

import dev.profitsoft.videogames.dto.developerDTOs.DeveloperDTO;
import dev.profitsoft.videogames.entity.DeveloperEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeveloperMapper {

    DeveloperDTO toDeveloperDTO(DeveloperEntity entity);

    DeveloperEntity toDeveloperEntity(DeveloperDTO dto);
}
