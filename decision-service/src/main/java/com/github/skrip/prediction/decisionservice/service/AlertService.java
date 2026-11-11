package com.github.skrip.prediction.decisionservice.service;

import com.github.skrip.prediction.decisionservice.config.AlertProperties;
import com.github.skrip.prediction.decisionservice.exception.IllegalTransitionException;
import com.github.skrip.prediction.decisionservice.exception.TransitionGuardFailedException;
import com.github.skrip.prediction.decisionservice.mapper.AlertMapper;
import com.github.skrip.prediction.decisionservice.model.Transition;
import com.github.skrip.prediction.decisionservice.model.dto.AlertDTO;
import com.github.skrip.prediction.decisionservice.model.entity.Alert;
import com.github.skrip.prediction.decisionservice.model.entity.AlertHistory;
import com.github.skrip.prediction.decisionservice.model.enums.AlertEvent;
import com.github.skrip.prediction.decisionservice.model.enums.AlertSeverity;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import com.github.skrip.prediction.decisionservice.model.enums.EventType;
import com.github.skrip.prediction.decisionservice.model.event.AlertCreatedEvent;
import com.github.skrip.prediction.decisionservice.model.repository.AlertHistoryRepository;
import com.github.skrip.prediction.decisionservice.model.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.orm.jpa.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Сервис для управления жизненным циклом алертов.
 * Включает: переходы состояний, запись в историю, метрики.
 */
@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;
    private final AlertStateMachine stateMachine;
    private final AlertHistoryRepository historyRepository;
    private final AlertMetrics alertMetrics;

    public AlertService(AlertRepository alertRepository,
                        AlertStateMachine stateMachine,
                        AlertHistoryRepository historyRepository,
                        AlertMetrics alertMetrics) {
        this.alertRepository = alertRepository;
        this.stateMachine = stateMachine;
        this.historyRepository = historyRepository;
        this.alertMetrics = alertMetrics;
    }

    /**
     * Выполняет переход алерта в новое состояние.
     */
    @Transactional
    public AlertDTO transition(Long alertId, AlertEvent event, Long actorUserId) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new IllegalArgumentException("Алерт не найден: " + alertId));

        AlertStatus from = alert.getStatus();
        AlertStatus to = stateMachine.transition(alert, event);

        alert.setStatus(to);
        alert = alertRepository.save(alert);

        // Записать в историю
        String comment = buildComment(event, alert);
        recordTransition(alert, from, to, event, actorUserId, comment);

        alertMetrics.recordTransition(to);

        log.info("Алерт {} перешёл из {} в {} по событию {}", alertId, from, to, event);

        return AlertMapper.toDTO(alert);
    }

    /**
     * Получить детализацию алерта с историей.
     */
    @Transactional(readOnly = true)
    public com.github.skrip.prediction.decisionservice.model.dto.AlertDetailsDTO getAlertDetails(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new IllegalArgumentException("Алерт не найден: " + alertId));

        List<AlertHistory> history = historyRepository.findByAlertIdOrderByCreatedAtDesc(alertId);
        return com.github.skrip.prediction.decisionservice.model.dto.AlertDetailsDTO.from(alert, history);
    }

    private void recordTransition(Alert alert, AlertStatus from, AlertStatus to,
                                  AlertEvent event, Long actorUserId, String comment) {
        AlertHistory record = AlertHistory.builder()
            .alertId(alert.getId())
            .fromStatus(from)
            .toStatus(to)
            .event(event)
            .actorUserId(actorUserId)
            .comment(comment)
            .createdAt(Instant.now())
            .build();
        historyRepository.save(record);
    }

    private String buildComment(AlertEvent event, Alert alert) {
        return switch (event) {
            case TIMEOUT -> "Просрочен без подтверждения";
            case MARK_FALSE -> "Данные помечены для переобучения модели";
            default -> event.name();
        };
    }
}
