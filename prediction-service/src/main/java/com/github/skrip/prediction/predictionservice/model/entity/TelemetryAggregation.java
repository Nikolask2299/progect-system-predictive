package com.github.skrip.prediction.predictionservice.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name ="telemetry_aggregation", schema = "prediction_schema")
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryAggregation {
    @EmbeddedId
    private TelemetryID id;
    private Long count;
    private double sum;
    private double mean;
    private double max;
    private double min;
    private double stdDev;

    @Embeddable
    public record TelemetryID(
        Integer sensorId,
        Instant timestamp
    ){}
}
