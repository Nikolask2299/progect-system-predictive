package com.github.skrip.prediction.predictionservice.utils;

import com.github.skrip.prediction.predictionservice.grpc.PredictionResponse;
import com.github.skrip.prediction.predictionservice.model.entity.PredictionAggregation;
import com.github.skrip.prediction.predictionservice.model.enums.PredictionSource;

import java.time.Instant;

public class PredictionMapper {

    public static PredictionAggregation toPredictionAggregation(PredictionResponse response) {
        var prediction = PredictionAggregation.builder()
                .device_id(response.getDeviceId())
                .calculate_at(Instant.ofEpochSecond(response.getCalculateAt().getSeconds(), response.getCalculateAt().getNanos()))
                .risk_score(response.getRiskScore())
                .rul_hours(response.getRulHours())
                .confidence(response.getConfidence())
                .maintenance_recommendation(response.getMaintenanceRecommendation())
                .source(PredictionSource.valueOf(response.getSource().name()));

        return prediction.build();
    }

}
