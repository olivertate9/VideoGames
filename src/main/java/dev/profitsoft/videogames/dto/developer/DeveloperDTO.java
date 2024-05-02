package dev.profitsoft.videogames.dto.developer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * Represents a DTO for DeveloperEntity.
 */
@Value
public class DeveloperDTO {

    /**
     * The name of the developer(required)
     */
    @NotBlank(message = "Name is required")
    String name;

    /**
     * The location of the developer HQ.
     */
    String location;

    /**
     * The year the developer studio was founded.
     * From 1889 to 2024.
     */
    @Min(value = 1889, message = "Year founded must be at least {value}")
    @Max(value = 2024, message = "Year founded should be less or equal to {value}")
    int yearFounded;

    /**
     * The number of employees in the developer's organization.
     * Must be at least 1.
     */
    @Min(value = 1, message = "Number of employees must be at least {value}")
    int numberOfEmployees;
}