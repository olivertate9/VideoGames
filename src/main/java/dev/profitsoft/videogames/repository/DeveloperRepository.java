package dev.profitsoft.videogames.repository;

import dev.profitsoft.videogames.entity.DeveloperEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<DeveloperEntity, Long> {
    Optional<DeveloperEntity> findByName(String developerName);

    boolean existsByName(String name);
}
