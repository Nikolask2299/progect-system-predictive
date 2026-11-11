package com.project.pdm.ingestion.ingestion_service.model.entity;

import com.project.pdm.ingestion.ingestion_service.model.dto.AggregationDTO;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name ="telemetry_aggregation", schema = "prediction_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public Object toaggregationDTO(){
        return new AggregationDTO(
                new AggregationDTO.TelemetryID(this.id.sensorId, this.id.timestamp),
                this.count,
                this.sum,
                this.mean,
                this.max,
                this.min,
                this.stdDev
        );
    }
}
