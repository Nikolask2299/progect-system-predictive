package com.project.pdm.ingestion.ingestion_service.model.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record TelemetryDTO(
        @NotNull(message = "Sensor ID required is not null")
        Integer sensor_id,
        @NotNull(message = "Timestamp required is not null")
        Instant timestamp,
        @NotNull
        double value
){}
