package dev.profitsoft.videogames.mapper;

import dev.profitsoft.videogames.dto.developer.DeveloperDTO;
import dev.profitsoft.videogames.entity.DeveloperEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between DeveloperEntity and DeveloperDTO.
 */
@Mapper(componentModel = "spring")
public interface DeveloperMapper {

    /**
     * Converts a DeveloperEntity to a DeveloperDTO.
     *
     * @param entity The DeveloperEntity to convert.
     * @return The DeveloperDTO.
     */
    DeveloperDTO toDeveloperDTO(DeveloperEntity entity);

    /**
     * Converts a DeveloperDTO to a DeveloperEntity.
     *
     * @param dto The DeveloperDTO to convert.
     * @return The DeveloperEntity.
     */
    @Mapping(target = "id", ignore = true)
    DeveloperEntity toDeveloperEntity(DeveloperDTO dto);
}
