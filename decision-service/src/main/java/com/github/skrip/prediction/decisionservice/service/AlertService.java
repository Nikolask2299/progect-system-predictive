package com.github.skrip.prediction.decisionservice.service;

import com.github.skrip.prediction.decisionservice.mapper.AlertMapper;
import com.github.skrip.prediction.decisionservice.model.dto.AlertDTO;
import com.github.skrip.prediction.decisionservice.model.dto.AlertDetailsDTO;
import com.github.skrip.prediction.decisionservice.model.entity.Alert;
import com.github.skrip.prediction.decisionservice.model.entity.AlertHistory;
import com.github.skrip.prediction.decisionservice.model.enums.AlertEvent;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import com.github.skrip.prediction.decisionservice.model.repository.AlertHistoryRepository;
import com.github.skrip.prediction.decisionservice.model.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Сервис для управления жизненным циклом алертов.
 * Включает: переходы состояний, запись в историю, метрики.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertStateMachine stateMachine;
    private final AlertHistoryRepository historyRepository;
    private final AlertMetrics alertMetrics;

    /**
     * Выполняет переход алерта в новое состояние.
     */
    @Transactional
    public AlertDTO transition(Long alertId, AlertEvent event, Long actorUserId) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));

        AlertStatus from = alert.getStatus();
        AlertStatus to = stateMachine.transition(alert, event);

        alert.setStatus(to);
        alert = alertRepository.save(alert);

        String comment = buildComment(event);
        recordTransition(alert, from, to, event, actorUserId, comment);

        alertMetrics.recordTransition(to);

        log.info("Alert {} transitioned: {} -> {} ({})", alertId, from, to, event);

        return AlertMapper.toDTO(alert);
    }

    /**
     * Получить детализацию алерта с историей.
     */
    @Transactional(readOnly = true)
    public AlertDetailsDTO getAlertDetails(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));

        List<AlertHistory> history = historyRepository.findByAlertIdOrderByCreatedAtDesc(alertId);
        return AlertDetailsDTO.from(alert, history);
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

        log.info("Alert history record: {}", record);
        historyRepository.save(record);
    }

    private String buildComment(AlertEvent event) {
        return switch (event) {
            case TIMEOUT -> "Expired without confirmation";
            case MARK_FALSE -> "The data is labeled for model retraining";
            default -> event.name();
        };
    }
}
