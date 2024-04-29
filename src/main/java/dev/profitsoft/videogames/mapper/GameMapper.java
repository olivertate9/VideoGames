package dev.profitsoft.videogames.mapper;

import dev.profitsoft.videogames.dto.gameDTOs.GameDTO;
import dev.profitsoft.videogames.dto.gameDTOs.GameInfoDTO;
import dev.profitsoft.videogames.dto.gameDTOs.GameUpdateDTO;
import dev.profitsoft.videogames.entity.GameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(target = "developerName", source = "developer.name")
    GameUpdateDTO toGameUpdateDTO(GameEntity gameEntity);

    GameDTO toGameDTO(GameEntity gameEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "developer", ignore = true)
    GameEntity toEntity(GameUpdateDTO dto);

    GameEntity toEntity(GameDTO dto);

    GameInfoDTO toGameInfoDTO(GameEntity gameEntity);
}
