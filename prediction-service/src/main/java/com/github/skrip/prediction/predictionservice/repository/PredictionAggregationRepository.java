package com.github.skrip.prediction.predictionservice.repository;

import com.github.skrip.prediction.predictionservice.model.entity.PredictionAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionAggregationRepository extends JpaRepository<PredictionAggregation, Long> {
}
