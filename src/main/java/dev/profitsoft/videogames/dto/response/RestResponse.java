package dev.profitsoft.videogames.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class RestResponse {
    String response;

    @JsonCreator
    public RestResponse(@JsonProperty("response") String response) {
        this.response = response;
    }
}
