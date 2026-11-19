package com.github.skrip.prediction.predictionservice.model.dto;

import com.github.skrip.prediction.predictionservice.model.enums.PredictionSource;

import java.time.Instant;

public record PredictionAggregationDto(
        Integer id,
        Integer deviceId,
        Double rulHours,
        Double confidence,
        Double riskScore,
        PredictionSource source,
        Instant calculateAt,
        String maintenanceRecommendation
) {}
