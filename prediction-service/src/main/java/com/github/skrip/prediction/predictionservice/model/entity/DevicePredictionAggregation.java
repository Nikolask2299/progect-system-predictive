package com.github.skrip.prediction.predictionservice.model.entity;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DevicePredictionAggregation {
    private Integer deviceId;
    private Instant windowStart;
    private Instant windowEnd;
    private List<TelemetryAggregation> predictions;
}
