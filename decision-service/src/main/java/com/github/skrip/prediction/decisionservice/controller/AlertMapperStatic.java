package com.github.skrip.prediction.decisionservice.controller;

import com.github.skrip.prediction.decisionservice.model.dto.AlertDTO;

/**
 * Статический маппер для Alert → AlertDTO.
 * Вынесен в отдельный класс для использования в контроллерах.
 */
final class AlertMapperStatic {

    private AlertMapperStatic() {}

    static AlertDTO toDTO(com.github.skrip.prediction.decisionservice.model.entity.Alert alert) {
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
}
