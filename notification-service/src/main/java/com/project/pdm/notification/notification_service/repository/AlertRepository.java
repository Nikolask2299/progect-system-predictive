package com.project.pdm.notification.notification_service.repository;

import com.project.pdm.notification.notification_service.model.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {

}
