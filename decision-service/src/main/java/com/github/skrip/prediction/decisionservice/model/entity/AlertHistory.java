package com.github.skrip.prediction.decisionservice.model.entity;

import com.github.skrip.prediction.decisionservice.model.enums.AlertEvent;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
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
@Table(name = "alert_history")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_id")
    private Long alertId;

    @Column(name = "from_status")
    @Enumerated(EnumType.STRING)
    private AlertStatus fromStatus;

    @Column(name = "to_status")
    @Enumerated(EnumType.STRING)
    private AlertStatus toStatus;

    @Enumerated(EnumType.STRING)
    private AlertEvent event;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    private String comment;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
