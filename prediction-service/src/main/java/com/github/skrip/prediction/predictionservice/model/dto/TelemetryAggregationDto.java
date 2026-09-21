package com.github.skrip.prediction.predictionservice.model.dto;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;

import java.time.Instant;

public record TelemetryAggregationDto(
    @EmbeddedId
    TelemetryID id,
    Long count,
    double sum,
    double mean,
    double max,
    double min,
    double stdDev
) {
    @Embeddable
    public record TelemetryID(
        Integer sensorId,
        Instant timestamp
    ){}
}