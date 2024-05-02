package dev.profitsoft.videogames.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "developer")
public class DeveloperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "year_founded")
    private int yearFounded;

    @Column(name = "number_of_employees")
    private int numberOfEmployees;
}