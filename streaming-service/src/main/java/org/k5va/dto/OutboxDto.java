package org.k5va.dto;

import java.time.LocalDateTime;

public record OutboxDto(
        String id,
        LocalDateTime createdAt,
        String payload
) {}
