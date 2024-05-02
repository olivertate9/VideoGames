package dev.profitsoft.videogames.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a developer entity in database.
 */
@Getter
@Setter
@Entity
@Table(name = "developer")
public class DeveloperEntity {

    /**
     * Unique id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Name of the developer or studio.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Location where developer HQ based.
     */
    @Column(name = "location")
    private String location;

    /**
     * Year the studio was founded.
     */
    @Column(name = "year_founded")
    private int yearFounded;

    /**
     * Number of employees in company.
     */
    @Column(name = "number_of_employees")
    private int numberOfEmployees;
}