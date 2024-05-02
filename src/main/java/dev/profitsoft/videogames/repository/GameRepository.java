package dev.profitsoft.videogames.repository;

import dev.profitsoft.videogames.entity.GameEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Long> {

    @Query(value = """
            SELECT g
            FROM GameEntity g
            WHERE (?1 IS NULL OR g.developer.id = ?1)
            AND (?2 IS NULL OR g.yearReleased = ?2)
            """, countQuery = """
            SELECT COUNT(g)
            FROM GameEntity g
            WHERE (?1 IS NULL OR g.developer.id = ?1)
            AND (?2 IS NULL OR g.yearReleased = ?2)
            """)
    Page<GameEntity> findGamesWithFilters(Long developerId, Integer yearReleased, Pageable pageable);

    @Query("""
            SELECT g
            FROM GameEntity g
            WHERE (?1 IS NULL OR g.developer.id = ?1)
            AND (?2 IS NULL OR g.yearReleased = ?2)
            """)
    List<GameEntity> findAllForReport(Long developerId, Integer yearReleased);
}
