package dev.profitsoft.videogames.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "game")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne()
    @JoinColumn(name = "developer_id", nullable = false)
    private DeveloperEntity developer;

    private int yearReleased;
    private String genre;

}