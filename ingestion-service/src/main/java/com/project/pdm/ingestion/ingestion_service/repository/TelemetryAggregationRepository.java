package com.project.pdm.ingestion.ingestion_service.repository;

import com.project.pdm.ingestion.ingestion_service.model.entity.TelemetryAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryAggregationRepository extends JpaRepository<TelemetryAggregation, TelemetryAggregation.TelemetryID> {
}
