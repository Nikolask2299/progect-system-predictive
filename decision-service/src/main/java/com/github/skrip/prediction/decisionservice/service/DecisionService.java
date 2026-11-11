package com.github.skrip.prediction.decisionservice.service;

import com.github.skrip.prediction.decisionservice.config.AlertProperties;
import com.github.skrip.prediction.decisionservice.model.entity.Alert;
import com.github.skrip.prediction.decisionservice.model.enums.AlertSeverity;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import com.github.skrip.prediction.decisionservice.model.enums.EventType;
import com.github.skrip.prediction.decisionservice.model.event.AlertCreatedEvent;
import com.github.skrip.prediction.decisionservice.model.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.orm.jpa.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;

@Service
public class DecisionService {

    private static final Logger log = LoggerFactory.getLogger(DecisionService.class);

    private final AlertProperties alertProperties;
    private final AlertRepository alertRepository;
    private final AlertMetrics alertMetrics;
    private final KafkaTemplate<String, AlertCreatedEvent> alertKafkaTemplate;

    public DecisionService(AlertProperties alertProperties,
                           AlertRepository alertRepository,
                           AlertMetrics alertMetrics,
                           KafkaTemplate<String, AlertCreatedEvent> alertKafkaTemplate) {
        this.alertProperties = alertProperties;
        this.alertRepository = alertRepository;
        this.alertMetrics = alertMetrics;
        this.alertKafkaTemplate = alertKafkaTemplate;
    }

    @Transactional
    public void evaluateRisk(Integer deviceId, Double value, String eventType) {
        if (value > alertProperties.getThresholdCritical()) {
            AlertSeverity severity = determineSeverity(value);
            String message = String.format("Превышен порог! value=%.1f, threshold=%.1f",
                value, alertProperties.getThresholdCritical());
            findOrCreateAlert(deviceId, eventType, severity, message);
            return;
        }
        log.debug("Значение {} не превышает порог {} для устройства {}",
            value, alertProperties.getThresholdCritical(), deviceId);
    }


    @Transactional
    public Alert findOrCreateAlert(Integer deviceId, String eventType,
                                   AlertSeverity severity, String message) {
        List<AlertStatus> activeStatuses = List.of(AlertStatus.NEW, AlertStatus.ACKNOWLEDGED);

        var active = alertRepository.findByDeviceIdAndEventTypeAndStatusIn(
            deviceId, eventType, activeStatuses);

        if (active.isPresent()) {
            // 2. Обновить счётчик существующего алерта
            Alert alert = active.get();
            alert.setOccurrenceCount(alert.getOccurrenceCount() + 1);
            alert.setLastTriggeredAt(Instant.now());
            alert = alertRepository.save(alert);
            alertMetrics.recordDeduplicated();
            log.info("Обновлён существующий алерт id={} для устройства {} (счётчик: {})",
                alert.getId(), deviceId, alert.getOccurrenceCount());
            return alert;
        }

        // 3. Создать новый алерт
        try {
            return createNewAlert(deviceId, eventType, severity, message);
        } catch (DataIntegrityViolationException e) {
            // 4. Гонка: другой поток уже создал алерт → повторить поиск
            log.warn("Гонка при создании алерта для устройства {}, eventType={}. Повторный поиск.",
                deviceId, eventType);
            return retryFindAndUpdate(deviceId, eventType, severity, message, 3);
        }
    }

    private Alert createNewAlert(Integer deviceId, String eventType,
                                 AlertSeverity severity, String message) {
        Alert alert = Alert.builder()
            .deviceId(deviceId)
            .status(AlertStatus.NEW)
            .severity(severity)
            .eventType(EventType.valueOf(eventType))
            .message(message)
            .timestamp(Instant.now())
            .lastTriggeredAt(Instant.now())
            .occurrenceCount(1)
            .assignedUserId(null) // Заглушка: назначение инженера — Этап 6+
            .relearningFlag(false)
            .build();

        alert = alertRepository.save(alert);

        log.info("Создан новый алерт id={} для устройства {} severity={}",
            alert.getId(), deviceId, severity);

        // Публикация события ТОЛЬКО при создании нового алерта (не при дедупликации)
        publishEventAfterCommit(alert);

        alertMetrics.recordCreated();

        return alert;
    }

    private void publishEventAfterCommit(Alert alert) {
        AlertCreatedEvent event = new AlertCreatedEvent(
            alert.getId(),
            alert.getDeviceId(),
            alert.getSeverity(),
            alert.getEventType(),
            alert.getTimestamp(),
            alert.getMessage()
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                alertKafkaTemplate.send("alerts.critical", event);
                log.info("Событие опубликовано в Kafka: alertId={}, deviceId={}",
                    event.alertId(), event.deviceId());
            }
        });
    }

    /**
     * Ретрайт при гонке: повторяет поиск и обновление до maxRetries раз.
     */
    private Alert retryFindAndUpdate(Integer deviceId, String eventType,
                                     AlertSeverity severity, String message, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            List<AlertStatus> activeStatuses = List.of(AlertStatus.NEW, AlertStatus.ACKNOWLEDGED);
            var existing = alertRepository.findByDeviceIdAndEventTypeAndStatusIn(
                deviceId, eventType, activeStatuses);

            if (existing.isPresent()) {
                Alert alert = existing.get();
                alert.setOccurrenceCount(alert.getOccurrenceCount() + 1);
                alert.setLastTriggeredAt(Instant.now());
                alert = alertRepository.save(alert);
                alertMetrics.recordDeduplicated();
                log.info("После ретрайта обновлён алерт id={} (счётчик: {})",
                    alert.getId(), alert.getOccurrenceCount());
                return alert;
            }

            // Повторная попытка создания
            try {
                return createNewAlert(deviceId, eventType, severity, message);
            } catch (DataIntegrityViolationException e) {
                log.debug("Ретрайт {} из {}: гонка продолжается", i + 1, maxRetries);
            }
        }

        throw new RuntimeException("Не удалось создать алерт после " + maxRetries + " попыток ретрайта");
    }

    /**
     * Определяет уровень серьёзности на основе значения.
     */
    private AlertSeverity determineSeverity(Double value) {
        if (value > alertProperties.getThresholdCritical() * 1.5) {
            return AlertSeverity.CRITICAL;
        } else if (value > alertProperties.getThresholdCritical() * 1.2) {
            return AlertSeverity.WARNING;
        }
        return AlertSeverity.INFO;
    }
}
