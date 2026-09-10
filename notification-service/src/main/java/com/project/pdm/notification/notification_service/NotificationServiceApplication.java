package com.project.pdm.notification.notification_service;

import com.project.pdm.notification.notification_service.model.entity.Alert;
import com.project.pdm.notification.notification_service.model.entity.Device;
import com.project.pdm.notification.notification_service.model.entity.Sensor;
import com.project.pdm.notification.notification_service.model.entity.TelemetryRecord;
import com.project.pdm.notification.notification_service.model.enums.AlertSeverity;
import com.project.pdm.notification.notification_service.repository.AlertRepository;
import com.project.pdm.notification.notification_service.repository.DeviceRepository;
import com.project.pdm.notification.notification_service.repository.SensorRepository;
import com.project.pdm.notification.notification_service.repository.TelemetryRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.Random;

@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);

	}

}
