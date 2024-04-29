package dev.profitsoft.videogames.dto.developer;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeveloperDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String location;
    private int yearFounded;
    private int numberOfEmployees;
}