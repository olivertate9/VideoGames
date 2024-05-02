package dev.profitsoft.videogames.repository;

import dev.profitsoft.videogames.entity.GameEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing CRUD operations on GameEntity entities.
 */
@Repository
public interface GameRepository extends JpaRepository<GameEntity, Long> {

    /**
     * Retrieves a page of GameEntity objects based on optional filtering criteria.
     *
     * @param developerId   The ID of the developer (optional).
     * @param yearReleased  The year of release (optional).
     * @param pageable      The pagination information for the result set.
     * @return A Page of GameEntity objects matching the specified filters.
     */
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

    /**
     * Retrieves a list of GameEntity objects based on optional filtering criteria, used for generating a report.
     *
     * @param developerId   The ID of the developer (optional).
     * @param yearReleased  The year of release (optional).
     * @return A List of GameEntity objects matching the specified filters.
     */
    @Query("""
            SELECT g
            FROM GameEntity g
            WHERE (?1 IS NULL OR g.developer.id = ?1)
            AND (?2 IS NULL OR g.yearReleased = ?2)
            """)
    List<GameEntity> findAllForReport(Long developerId, Integer yearReleased);
}
