package dev.profitsoft.videogames.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a game entity in the application.
 */
@Getter
@Setter
@Entity
@Table(name = "game")
public class GameEntity {

    /**
     * The unique ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * The game title.
     */
    @Column(nullable = false)
    private String title;

    /**
     * The developer of the game (many-to-one relationship with DeveloperEntity).
     */
    @ManyToOne
    @JoinColumn(name = "developer_id", nullable = false)
    private DeveloperEntity developer;

    /**
     * The year the game was released.
     */
    @Column(name = "year_released")
    private int yearReleased;

    /**
     * The genre of the game.
     */
    @Column(name = "genre")
    private String genre;
}