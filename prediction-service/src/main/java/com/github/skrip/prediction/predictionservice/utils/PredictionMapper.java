package com.github.skrip.prediction.predictionservice.utils;

import com.github.skrip.prediction.predictionservice.grpc.PredictionRequest;
import com.github.skrip.prediction.predictionservice.grpc.PredictionResponse;
import com.github.skrip.prediction.predictionservice.grpc.SensorAggregation;
import com.github.skrip.prediction.predictionservice.model.entity.PredictionAggregation;
import com.github.skrip.prediction.predictionservice.model.entity.TelemetryAggregation;
import com.github.skrip.prediction.predictionservice.model.enums.PredictionSource;

import java.time.Instant;
import java.util.List;

import static com.github.skrip.prediction.predictionservice.utils.TimeUtils.convertToInstant;
import static com.github.skrip.prediction.predictionservice.utils.TimeUtils.convertToTimestamp;

public class PredictionMapper {

    public static PredictionAggregation toPredictionAggregation(PredictionResponse response) {
        var prediction = PredictionAggregation.builder()
                .device_id(response.getDeviceId())
                .calculate_at(convertToInstant(response.getCalculateAt()))
                .risk_score(response.getRiskScore())
                .rul_hours(response.getRulHours())
                .confidence(response.getConfidence())
                .maintenance_recommendation(response.getMaintenanceRecommendation())
                .source(PredictionSource.valueOf(response.getSource().name()));

        return prediction.build();
    }

    public static PredictionRequest toPredictionResponse(Long deviceId, Instant windowStart, Instant windowEnd, List<TelemetryAggregation> predictions) {
        return PredictionRequest.newBuilder()
                .setDeviceId(deviceId)
                .setWindowStart(convertToTimestamp(windowStart))
                .setWindowEnd(convertToTimestamp(windowEnd))
                .addAllSensors(predictions.stream()
                        .map(PredictionMapper::toSensorAggregation)
                        .toList())
                .build();
    }

    public static SensorAggregation toSensorAggregation(TelemetryAggregation prediction) {
        return SensorAggregation.newBuilder()
                    .setSensorId(prediction.getId().sensorId())
                    .setTimestamp(convertToTimestamp(prediction.getId().timestamp()))
                    .setCount(prediction.getCount())
                    .setSum(prediction.getSum())
                    .setMean(prediction.getMean())
                    .setMax(prediction.getMax())
                    .setMin(prediction.getMin())
                    .setStdDev(prediction.getStdDev())
                    .build();
    }

}
