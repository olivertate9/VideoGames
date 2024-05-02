package dev.profitsoft.videogames.repository;

import dev.profitsoft.videogames.entity.DeveloperEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing CRUD operations on DeveloperEntity entities.
 */
@Repository
public interface DeveloperRepository extends JpaRepository<DeveloperEntity, Long> {

    /**
     * Finds a developer entity by name.
     *
     * @param developerName The name of the developer to search for.
     * @return An Optional containing the DeveloperEntity if found, or empty if not found.
     */
    Optional<DeveloperEntity> findByName(String developerName);
}
