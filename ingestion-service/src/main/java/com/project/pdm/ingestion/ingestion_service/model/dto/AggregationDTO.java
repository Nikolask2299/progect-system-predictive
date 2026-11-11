package com.project.pdm.ingestion.ingestion_service.model.dto;

import java.time.Instant;

public record AggregationDTO(
        TelemetryID id,
        Long count,
        double sum,
        double mean,
        double max,
        double min,
        double stdDev
) {
    public record TelemetryID(
            Integer sensorId,
            Instant timestamp
    ){}
}
