package com.project.pdm.ingestion.ingestion_service.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name ="telemetry_aggregation")
public record TelemetryAggregation(
    @EmbeddedId
    TelemetryID id,
    Long count,
    double sum,
    double mean,
    double max,
    double min,
    double stdDev
){
    @Embeddable
    public record TelemetryID(
        Integer sensorId,
        Instant timestamp
    ){}
}
