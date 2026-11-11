package com.github.skrip.prediction.decisionservice.model.dto;

import com.github.skrip.prediction.decisionservice.model.entity.AlertHistory;

import java.time.Instant;
import java.util.List;

public record AlertDetailsDTO(
    Long id,
    Integer deviceId,
    Integer sensorId,
    String status,
    String severity,
    String eventType,
    String message,
    Integer occurrenceCount,
    Instant timestamp,
    Instant lastTriggeredAt,
    Instant acknowledgedAt,
    Instant resolvedAt,
    Instant expiredAt,
    Long assignedUserId,
    Boolean relearningFlag,
    List<HistoryEntry> history
) {
    public record HistoryEntry(
        Long id,
        String fromStatus,
        String toStatus,
        String event,
        Long actorUserId,
        String comment,
        Instant createdAt
    ) {}

    public static AlertDetailsDTO from(com.github.skrip.prediction.decisionservice.model.entity.Alert alert,
                                       List<AlertHistory> history) {
        List<HistoryEntry> historyEntries = history.stream()
            .map(h -> new HistoryEntry(
                h.getId(),
                h.getFromStatus() != null ? h.getFromStatus().name() : null,
                h.getToStatus().name(),
                h.getEvent() != null ? h.getEvent().name() : null,
                h.getActorUserId(),
                h.getComment(),
                h.getCreatedAt()
            ))
            .toList();

        return new AlertDetailsDTO(
            alert.getId(),
            alert.getDeviceId(),
            alert.getSensorId(),
            alert.getStatus().name(),
            alert.getSeverity().name(),
            alert.getEventType().name(),
            alert.getMessage(),
            alert.getOccurrenceCount(),
            alert.getTimestamp(),
            alert.getLastTriggeredAt(),
            alert.getAcknowledgedAt(),
            alert.getResolvedAt(),
            alert.getExpiredAt(),
            alert.getAssignedUserId(),
            alert.getRelearningFlag(),
            historyEntries
        );
    }
}
