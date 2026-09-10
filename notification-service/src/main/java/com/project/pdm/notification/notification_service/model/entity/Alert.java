package com.project.pdm.notification.notification_service.model.entity;

import com.project.pdm.notification.notification_service.model.enums.AlertSeverity;
import com.project.pdm.notification.notification_service.model.enums.AlertStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "alerts", schema = "prediction_schema")
@AllArgsConstructor
@NoArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Instant createAt;

    private String message;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private AlertSeverity severity;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private AlertStatus status;

    @ManyToOne
    @JoinColumn(
            name = "device_id",
            referencedColumnName = "id"
    )
    private Device device;

    @ManyToOne
    @JoinColumn(
            name = "sensor_id",
            referencedColumnName = "id"
    )
    private Sensor sensor;
}
