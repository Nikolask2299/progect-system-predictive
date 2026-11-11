package com.github.skrip.prediction.decisionservice.model.event;

import com.github.skrip.prediction.decisionservice.model.enums.AlertSeverity;
import com.github.skrip.prediction.decisionservice.model.enums.EventType;

import java.time.Instant;

/**
 * Событие, публикуемое в Kafka при создании нового алерта.
 * Контракт соответствует docs/events/alerts-critical.md.
 * Публикуется ТОЛЬКО при создании нового алерта, не при дедупликации.
 */
public record AlertCreatedEvent(
    Long alertId,
    Integer deviceId,
    AlertSeverity severity,
    EventType eventType,
    Instant timestamp,
    String message
) {}
