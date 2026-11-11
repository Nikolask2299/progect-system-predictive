package com.github.skrip.prediction.decisionservice.service;

import com.github.skrip.prediction.decisionservice.config.AlertProperties;
import com.github.skrip.prediction.decisionservice.model.enums.AlertEvent;
import com.github.skrip.prediction.decisionservice.model.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Сканер для автоматического перевода просроченных алертов в статус EXPIRED.
 * Запускается по расписанию (ALR-16).
 * Идемпотентен: при повторном запуске не вызывает ошибок.
 */
@Component
public class AlertExpirationScanner {

    private static final Logger log = LoggerFactory.getLogger(AlertExpirationScanner.class);

    private final AlertRepository alertRepository;
    private final AlertService alertService;
    private final AlertProperties alertProperties;

    public AlertExpirationScanner(AlertRepository alertRepository,
                                  AlertService alertService,
                                  AlertProperties alertProperties) {
        this.alertRepository = alertRepository;
        this.alertService = alertService;
        this.alertProperties = alertProperties;
    }

    @Scheduled(fixedDelayString = "${pdm.alert.expiration-scan-interval-ms}")
    public void scanAndExpire() {
        Instant cutoff = Instant.now().minus(
            alertProperties.getExpirationThresholdHours(), ChronoUnit.HOURS);

        List<com.github.skrip.prediction.decisionservice.model.entity.Alert> expired =
            alertRepository.findExpiredNewAlerts(cutoff);

        if (expired.isEmpty()) {
            log.debug("Просроченные алерты не найдены (cutoff={})", cutoff);
            return;
        }

        log.info("Найдено {} просроченных алертов для обработки", expired.size());

        for (com.github.skrip.prediction.decisionservice.model.entity.Alert alert : expired) {
            try {
                alertService.transition(alert.getId(), AlertEvent.TIMEOUT, null);
                log.info("Алерт {} переведён в EXPIRED (просрочен с {})",
                    alert.getId(), alert.getTimestamp());
            } catch (Exception e) {
                // Переход NEW → EXPIRED защищён автоматом (нелегальный переход не сработает)
                // Это защищает от дублирования при повторных запусках сканера
                log.warn("Не удалось перевести алерт {} в EXPIRED: {}",
                    alert.getId(), e.getMessage());
            }
        }
    }
}
