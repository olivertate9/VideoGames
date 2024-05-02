package dev.profitsoft.videogames.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Represents a DTO for REST responses.
 */
@Value
public class RestResponse {
    /**
     * The response message.
     */
    String response;

    @JsonCreator
    public RestResponse(@JsonProperty("response") String response) {
        this.response = response;
    }
}
