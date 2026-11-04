package com.github.skrip.prediction.predictionservice.model.entity;

import com.github.skrip.prediction.predictionservice.model.dto.TelemetryAggregationDto;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name ="telemetry_aggregation", schema = "prediction_schema")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TelemetryAggregation {
    @EmbeddedId
    private TelemetryID id;
    private Long count;
    private double sum;
    private double mean;
    private double max;
    private double min;
    private double stdDev;

    @Embeddable
    public record TelemetryID(
        Integer sensorId,
        Instant timestamp
    ){}

    public static TelemetryAggregation toEntity(TelemetryAggregationDto telemetryAggregationDto) {
        if (telemetryAggregationDto != null) {
            return new TelemetryAggregation(
                    new TelemetryID(telemetryAggregationDto.id().sensorId(), telemetryAggregationDto.id().timestamp()),
                    telemetryAggregationDto.count(),
                    telemetryAggregationDto.sum(),
                    telemetryAggregationDto.mean(),
                    telemetryAggregationDto.max(),
                    telemetryAggregationDto.min(),
                    telemetryAggregationDto.stdDev()
            );
        }
        return null;
    }
}
