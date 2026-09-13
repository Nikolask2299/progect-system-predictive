package com.github.skrip.prediction.dto;

import java.time.Instant;

public record TelemetryDTO(
        Integer sensor_id,
        Instant timestamp,
        double value
){}
