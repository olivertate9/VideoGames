package dev.profitsoft.videogames.dto.developer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class DeveloperDTO {

    @NotBlank(message = "Name is required")
    String name;

    String location;

    @Max(value = 2024, message = "Year founded should be less or equal to {value}")
    int yearFounded;

    @Min(value = 1, message = "Number of employees must be at least {value}")
    int numberOfEmployees;
}