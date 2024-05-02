package dev.profitsoft.videogames.mapper;

import dev.profitsoft.videogames.dto.game.GameDTO;
import dev.profitsoft.videogames.dto.game.GameInfoDTO;
import dev.profitsoft.videogames.dto.game.GameUpdateDTO;
import dev.profitsoft.videogames.entity.GameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between GameEntity and various GameDTOs.
 */
@Mapper(componentModel = "spring")
public interface GameMapper {

    /**
     * Converts a GameEntity to a GameUpdateDTO.
     *
     * @param gameEntity The GameEntity to convert.
     * @return The GameUpdateDTO.
     */
    @Mapping(target = "developerName", source = "developer.name")
    GameUpdateDTO toGameUpdateDTO(GameEntity gameEntity);

    /**
     * Converts a GameEntity to a GameDTO.
     *
     * @param gameEntity The GameEntity to convert.
     * @return The GameDTO.
     */
    GameDTO toGameDTO(GameEntity gameEntity);

    /**
     * Converts a GameUpdateDTO to a GameEntity, ignoring 'id' and 'developer' properties.
     *
     * @param dto The GameUpdateDTO to convert.
     * @return The GameEntity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "developer", ignore = true)
    GameEntity toGameEntity(GameUpdateDTO dto);

    /**
     * Converts a GameEntity to a GameInfoDTO.
     *
     * @param gameEntity The GameEntity to convert.
     * @return The GameInfoDTO.
     */
    GameInfoDTO toGameInfoDTO(GameEntity gameEntity);
}
