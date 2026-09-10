package com.project.pdm.notification.notification_service.repository;

import com.project.pdm.notification.notification_service.model.entity.TelemetryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryRepository extends JpaRepository<TelemetryRecord, TelemetryRecord.TelemetryId> {
}
