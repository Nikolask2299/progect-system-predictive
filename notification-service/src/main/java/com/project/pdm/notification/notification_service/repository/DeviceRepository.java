package com.project.pdm.notification.notification_service.repository;

import com.project.pdm.notification.notification_service.model.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
