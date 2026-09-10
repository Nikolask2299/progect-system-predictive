package com.project.pdm.notification.notification_service.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "telemetry", schema = "prediction_schema")
public class TelemetryRecord {

        @EmbeddedId
        private TelemetryId id;

        @MapsId("sensorId")
        @ManyToOne
        @JoinColumn(name = "sensor_id")
        private Sensor sensor;

        private Double value;
        private String quality;


    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TelemetryId {
       private Instant time;
       private Integer sensorId;
    }
}
