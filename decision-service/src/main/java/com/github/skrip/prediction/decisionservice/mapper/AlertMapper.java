package com.github.skrip.prediction.decisionservice.mapper;

import com.github.skrip.prediction.decisionservice.model.dto.AlertDetailsDTO;
import com.github.skrip.prediction.decisionservice.model.dto.AlertDTO;
import com.github.skrip.prediction.decisionservice.model.entity.Alert;

/**
 * Маппер для преобразования Alert в DTO и обратно.
 */
public final class AlertMapper {

    private AlertMapper() {}

    public static AlertDTO toDTO(Alert alert) {
        return new AlertDTO(
            alert.getId(),
            alert.getDeviceId(),
            alert.getSensorId(),
            alert.getStatus(),
            alert.getSeverity(),
            alert.getEventType(),
            alert.getMessage(),
            alert.getOccurrenceCount(),
            alert.getTimestamp(),
            alert.getLastTriggeredAt(),
            alert.getAssignedUserId()
        );
    }

    public static AlertDetailsDTO toDetailsDTO(Alert alert, com.github.skrip.prediction.decisionservice.model.entity.AlertHistory history) {
        return AlertDetailsDTO.from(alert, java.util.List.of(history));
    }
}
