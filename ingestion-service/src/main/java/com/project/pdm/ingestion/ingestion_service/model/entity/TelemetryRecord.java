package com.project.pdm.ingestion.ingestion_service.model.entity;


import com.project.pdm.ingestion.ingestion_service.model.dto.TelemetryDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Table(name = "telemetry")
@AllArgsConstructor
@NoArgsConstructor
public class TelemetryRecord {

    @EmbeddedId
    private TelemetryID id;

    private double value;
    private String quality;

    public Integer getSensorId() {
        return id != null ? id.sensorId() : null;
    }

    public Instant getTimestamp() {
        return id != null ? id.timestamp() : null;
    }

    @Embeddable
    public record TelemetryID(
            @Column(name = "time") Instant timestamp,
        Integer sensorId
    ){}

    public static TelemetryRecord toEntity(TelemetryDTO dto) {
        return new TelemetryRecord(
                new TelemetryID(dto.timestamp(), dto.sensor_id()),
                dto.value(), "QUALITY"
        );
    }
}
