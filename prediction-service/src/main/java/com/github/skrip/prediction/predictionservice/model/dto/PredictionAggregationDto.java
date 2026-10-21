package com.github.skrip.prediction.predictionservice.model.dto;

import com.github.skrip.prediction.predictionservice.model.enums.PredictionSource;

import java.time.Instant;

public record PredictionAggregationDto(
        Long id,
        Long device_id,
        Double rul_hours,
        Double confidence,
        Double risk_score,
        PredictionSource source,
        Instant calculate_at,
        String maintenance_recommendation
) {}
