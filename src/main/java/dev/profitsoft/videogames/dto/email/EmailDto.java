package dev.profitsoft.videogames.dto.email;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class EmailDto {
    private String id;

    private String subject;

    private String content;

    private String email;

    private Status status;

    private String errorMessage;

    private int attemptCount;

    private Instant lastAttempt;
}
