package com.github.skrip.prediction.predictionservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class DevicePredictionAggregation {
    private Long deviceId;
    private Instant windowStart;
    private Instant windowEnd;
    private List<TelemetryAggregation> predictions;
}
