package com.unibuc.backend.exception;

import lombok.*;

import java.time.Instant;

@Data
@RequiredArgsConstructor
@Builder
public class ErrorResponse {
    private final String message;
    private final Instant timestamp = Instant.now();
}
