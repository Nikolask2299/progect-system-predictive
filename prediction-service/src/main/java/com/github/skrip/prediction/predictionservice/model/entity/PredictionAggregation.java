package com.github.skrip.prediction.predictionservice.model.entity;


import com.github.skrip.prediction.predictionservice.model.dto.PredictionAggregationDto;
import com.github.skrip.prediction.predictionservice.model.enums.PredictionSource;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "prediction_aggregation")
public class PredictionAggregation {
    @Id
    private Long id;

    private Long device_id;

    private Double rul_hours;

    private Double confidence;

    private Double risk_score;

    @Enumerated(EnumType.STRING)
    private PredictionSource source;

    private Instant calculate_at;

    private String maintenance_recommendation;

    public PredictionAggregationDto aggregationDto() {
        return new PredictionAggregationDto(
                id,
                device_id,
                rul_hours,
                confidence,
                risk_score,
                source,
                calculate_at,
                maintenance_recommendation
        );
    }
}
