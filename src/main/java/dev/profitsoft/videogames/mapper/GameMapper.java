package dev.profitsoft.videogames.mapper;

import dev.profitsoft.videogames.dto.game.GameDTO;
import dev.profitsoft.videogames.dto.game.GameInfoDTO;
import dev.profitsoft.videogames.dto.game.GameUpdateDTO;
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
    GameEntity toGameEntity(GameUpdateDTO dto);

    GameInfoDTO toGameInfoDTO(GameEntity gameEntity);
}
