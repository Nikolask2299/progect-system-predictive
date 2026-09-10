package com.project.pdm.ingestion.ingestion_service.repository;

import com.project.pdm.ingestion.ingestion_service.model.entity.TelemetryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TelemetryRepository extends JpaRepository<TelemetryRecord, TelemetryRecord.TelemetryID>{

   //boolean existsBySensorId(@Param("sensorId") Integer sensorId);

   //boolean existBySensorIdAndTimestamp(TelemetryRecord.TelemetryID id);

   //List<TelemetryRecord> findBySensorIdAndTimestampAfter(Integer sensorId, Instant timestamp);

  // List<TelemetryRecord> findTopBySensorIdOrderByTimestampDesc(Integer sensorId);

}
