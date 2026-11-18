package com.github.skrip.prediction.decisionservice.controller;

import com.github.skrip.prediction.decisionservice.model.dto.AlertDTO;
import com.github.skrip.prediction.decisionservice.model.dto.AlertDetailsDTO;
import com.github.skrip.prediction.decisionservice.model.entity.Alert;
import com.github.skrip.prediction.decisionservice.model.enums.AlertEvent;
import com.github.skrip.prediction.decisionservice.model.enums.AlertSeverity;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import com.github.skrip.prediction.decisionservice.model.enums.EventType;
import com.github.skrip.prediction.decisionservice.model.repository.AlertRepository;
import com.github.skrip.prediction.decisionservice.service.AlertService;
import com.github.skrip.prediction.decisionservice.service.DecisionService;
import com.github.skrip.prediction.decisionservice.specification.AlertSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;
    private final AlertRepository alertRepository;
    private final DecisionService decisionService;

    @GetMapping
    public ResponseEntity<Page<AlertDTO>> listAlerts(
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) Integer deviceId,
            @RequestParam(required = false) AlertSeverity severity,
            @RequestParam(required = false) Instant fromDate,
            @RequestParam(required = false) Instant toDate,
            Pageable pageable) {
        Specification<Alert> spec = AlertSpecification.withFilters(status, deviceId, severity, fromDate, toDate);
        Page<AlertDTO> alerts = alertRepository.findAll(spec, pageable)
            .map(AlertMapperStatic::toDTO);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertDetailsDTO> getAlertDetails(@PathVariable Long id) {
        AlertDetailsDTO details = alertService.getAlertDetails(id);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<AlertDTO> confirm(@PathVariable Long id) {
        AlertDTO result = alertService.transition(id, AlertEvent.CONFIRM, null);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/start_work")
    public ResponseEntity<AlertDTO> startWork(@PathVariable Long id) {
        AlertDTO result = alertService.transition(id, AlertEvent.START_WORK, null);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<AlertDTO> resolve(@PathVariable Long id) {
        AlertDTO result = alertService.transition(id, AlertEvent.RESOLVE, null);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/mark_false_positive")
    public ResponseEntity<AlertDTO> markFalsePositive(@PathVariable Long id) {
        AlertDTO result = alertService.transition(id, AlertEvent.MARK_FALSE, null);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/telemetry")
    public ResponseEntity<Void> handleTelemetry(@RequestBody TelemetryRecord record) {
        decisionService.evaluateRisk(record.deviceId(), record.value(), EventType.THRESHOLD_EXCEEDED);
        return ResponseEntity.ok().build();
    }

    public record TelemetryRecord(Integer deviceId, Integer sensorId, Double value) {}
}
