package com.github.skrip.prediction.decisionservice.service;

import com.github.skrip.prediction.decisionservice.config.AlertProperties;
import com.github.skrip.prediction.decisionservice.model.entity.Alert;
import com.github.skrip.prediction.decisionservice.model.enums.AlertSeverity;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import com.github.skrip.prediction.decisionservice.model.enums.EventType;
import com.github.skrip.prediction.decisionservice.model.event.AlertCreatedEvent;
import com.github.skrip.prediction.decisionservice.model.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DecisionService {
    private static final List<AlertStatus> activeStatuses = List.of(AlertStatus.NEW, AlertStatus.ACKNOWLEDGED);

    private final AlertProperties alertProperties;
    private final AlertRepository alertRepository;
    private final AlertMetrics alertMetrics;
    private final KafkaTemplate<String, AlertCreatedEvent> alertKafkaTemplate;

    @Transactional
    public void evaluateRisk(Integer deviceId, Double value, EventType eventType) {
        if (value > alertProperties.getThresholdCritical()) {
            AlertSeverity severity = determineSeverity(value);
            String message = String.format("The threshold has been exceeded! value=%.1f, threshold=%.1f",
                value, alertProperties.getThresholdCritical());
            findOrCreateAlert(deviceId, eventType, severity, message);
            return;
        }

        log.debug("Value {} is below critical threshold {} for device {}",
            value, alertProperties.getThresholdCritical(), deviceId);
    }

    @Transactional
    public void findOrCreateAlert(Integer deviceId, EventType eventType, AlertSeverity severity, String message) {

        var active = alertRepository.findByDeviceIdAndEventTypeAndStatusIn(deviceId, eventType, activeStatuses);

        if (active.isPresent()) {
            Alert alert = active.get();
            alert.setOccurrenceCount(alert.getOccurrenceCount() + 1);
            alert.setLastTriggeredAt(Instant.now());
            alert = alertRepository.save(alert);

            alertMetrics.recordDeduplicated();
            log.info("Updated existing alert id={} for device {} (occurrence count: {})",
                alert.getId(), deviceId, alert.getOccurrenceCount());
            return;
        }

        try {
            createNewAlert(deviceId, eventType, severity, message);
        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition detected for device {} and event type {}. Retrying...", deviceId, eventType);
            retryFindAndUpdate(deviceId, eventType, severity, message, 3);
        }
    }

    private void createNewAlert(Integer deviceId, EventType eventType, AlertSeverity severity, String message) {
        Alert alert = Alert.builder()
            .deviceId(deviceId)
            .status(AlertStatus.NEW)
            .severity(severity)
            .eventType(eventType)
            .message(message)
            .createdAt(Instant.now())
            .lastTriggeredAt(Instant.now())
            .occurrenceCount(1)
            .assignedUserId(null)
            .build();

        alert = alertRepository.save(alert);

        log.info("Created new alert id={} for device {} with severity {}", alert.getId(), deviceId, severity);

        publishEventAfterCommit(alert);
        alertMetrics.recordCreated();
    }

    private void publishEventAfterCommit(Alert alert) {
        AlertCreatedEvent event = new AlertCreatedEvent(
            alert.getId(),
            alert.getDeviceId(),
            alert.getSeverity(),
            alert.getEventType(),
            alert.getCreatedAt(),
            alert.getMessage()
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                alertKafkaTemplate.send("alerts.critical", event);
                log.info("Event sent to Kafka: alertId={}, deviceId={}",
                    event.alertId(), event.deviceId());
            }
        });
    }

    private void retryFindAndUpdate(Integer deviceId, EventType eventType, AlertSeverity severity, String message, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            var existing = alertRepository.findByDeviceIdAndEventTypeAndStatusIn(deviceId, eventType, activeStatuses);

            if (existing.isPresent()) {
                Alert alert = existing.get();
                alert.setOccurrenceCount(alert.getOccurrenceCount() + 1);
                alert.setLastTriggeredAt(Instant.now());
                alert = alertRepository.save(alert);
                alertMetrics.recordDeduplicated();
                log.info("Updated filed existing alert id={} (count: {})",
                    alert.getId(), alert.getOccurrenceCount());
                return;
            }

            try {
                createNewAlert(deviceId, eventType, severity, message);
                return;
            } catch (DataIntegrityViolationException e) {
                log.debug("Retry {} failed maxRetries={}", i + 1, maxRetries);
            }
        }

        throw new RuntimeException("No alert created after max retries: " + maxRetries);
    }

    private AlertSeverity determineSeverity(Double value) {
        if (value > alertProperties.getThresholdCritical() * 1.5) {
            return AlertSeverity.CRITICAL;
        } else if (value > alertProperties.getThresholdCritical() * 1.2) {
            return AlertSeverity.WARNING;
        }
        return AlertSeverity.INFO;
    }
}
