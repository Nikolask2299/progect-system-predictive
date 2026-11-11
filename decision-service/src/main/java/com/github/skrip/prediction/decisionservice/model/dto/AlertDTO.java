package com.github.skrip.prediction.decisionservice.model.dto;

import com.github.skrip.prediction.decisionservice.model.enums.AlertSeverity;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import com.github.skrip.prediction.decisionservice.model.enums.EventType;

import java.time.Instant;

public record AlertDTO(
    Long id,
    Integer deviceId,
    Integer sensorId,
    AlertStatus status,
    AlertSeverity severity,
    EventType eventType,
    String message,
    Integer occurrenceCount,
    Instant createdAt,
    Instant lastTriggeredAt,
    Long assignedUserId
) {}
