package com.github.skrip.prediction.decisionservice.model.entity;

import com.github.skrip.prediction.decisionservice.model.enums.AlertSeverity;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import com.github.skrip.prediction.decisionservice.model.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "alerts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"device_id", "event_type", "status"}, name = "uk_active_alert")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Integer deviceId;

    @Column(name = "sensor_id")
    private Integer sensorId;

    @Enumerated(EnumType.STRING)
    private AlertStatus status;

    @Column(name = "severity")
    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;

    @Column(name = "event_type")
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(length = 1000)
    private String message;

    @Column(name = "occurrence_count")
    @Builder.Default
    private Integer occurrenceCount = 1;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "last_triggered_at")
    private Instant lastTriggeredAt;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "assigned_user_id")
    private Long assignedUserId;
}
