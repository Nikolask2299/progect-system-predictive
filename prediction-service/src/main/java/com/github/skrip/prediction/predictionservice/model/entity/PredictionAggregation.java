package com.github.skrip.prediction.predictionservice.model.entity;


import com.github.skrip.prediction.predictionservice.model.dto.PredictionAggregationDto;
import com.github.skrip.prediction.predictionservice.model.enums.PredictionSource;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "prediction_aggregation", schema = "prediction_schema")
public class PredictionAggregation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "device_id")
    private Integer deviceId;

    @Column(name = "rul_hours")
    private Double rulHours;

    private Double confidence;

    @Column(name = "risk_score")
    private Double riskScore;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    private PredictionSource source;

    @Column(name = "calculate_at")
    private Instant calculateAt;

    @Column(name = "maintenance_recommendation")
    private String maintenanceRecommendation;

    public PredictionAggregationDto aggregationDto() {
        return new PredictionAggregationDto(
                id,
                deviceId,
                rulHours,
                confidence,
                riskScore,
                source,
                calculateAt,
                maintenanceRecommendation
        );
    }
}
