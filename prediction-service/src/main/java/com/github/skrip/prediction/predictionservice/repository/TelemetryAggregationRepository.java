package com.github.skrip.prediction.predictionservice.repository;

import com.github.skrip.prediction.predictionservice.model.entity.TelemetryAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TelemetryAggregationRepository extends JpaRepository<TelemetryAggregation, TelemetryAggregation.TelemetryID> {

    List<TelemetryAggregation> findById_SensorIdInAndId_TimestampBetween(List<Integer> sensorIds, Instant startTimestamp, Instant endTimestamp);
}
