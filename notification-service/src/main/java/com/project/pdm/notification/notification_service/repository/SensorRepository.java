package com.project.pdm.notification.notification_service.repository;

import com.project.pdm.notification.notification_service.model.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
}
