package com.github.skrip.prediction.predictionservice.repository;

import com.github.skrip.prediction.predictionservice.model.entity.PredictionAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PredictionAggregationRepository extends JpaRepository<PredictionAggregation, Integer> {
    Optional<PredictionAggregation> findFirstByDeviceIdOrderByCalculateAtDesc(Integer deviceId);
    PredictionAggregation findByDeviceIdAndCalculateAtAfter(Long device_id, Instant after);
}
