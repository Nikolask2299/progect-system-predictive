package com.project.pdm.notification.notification_service;

import com.project.pdm.notification.notification_service.model.entity.Alert;
import com.project.pdm.notification.notification_service.model.entity.Device;
import com.project.pdm.notification.notification_service.model.entity.Sensor;
import com.project.pdm.notification.notification_service.model.entity.TelemetryRecord;
import com.project.pdm.notification.notification_service.model.enums.AlertSeverity;
import com.project.pdm.notification.notification_service.model.enums.AlertStatus;
import com.project.pdm.notification.notification_service.repository.AlertRepository;
import com.project.pdm.notification.notification_service.repository.DeviceRepository;
import com.project.pdm.notification.notification_service.repository.SensorRepository;
import com.project.pdm.notification.notification_service.repository.TelemetryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@RequiredArgsConstructor
@Component
public class Init {

    public final DeviceRepository deviceRepository;
    public final SensorRepository sensorRepository;
    public final AlertRepository alertRepository;
    public final TelemetryRepository telemetryRepository;


    @PostConstruct
    public void init() {
        Random rnd = new Random();

        for (int i = 0; i < 10; i++) {
            Device device = new Device();
            device.setName("DEVICE_" + i);
            device.setType("TYPE_" + i);
            device.setLocationId("LOCATION_" + i);
            device.setInstallDate(Instant.now());
            deviceRepository.save(device);
            for (int j = 0; j < 5; j++) {
                Sensor sensor = new Sensor();
                sensor.setCalibrationDate(Instant.now());
                sensor.setUnit("CELSIUS");
                sensor.setType("TYPE_" + j);
                sensor.setDevice(device);
                sensorRepository.save(sensor);

                Alert alert = new Alert();
                alert.setSeverity(AlertSeverity.INFO);
                alert.setStatus(AlertStatus.EXPIRED);
                alert.setCreateAt(Instant.now());
                alert.setMessage("ALERT_" + j);
                alert.setSensor(sensor);
                alert.setDevice(device);
                alertRepository.save(alert);

                for (int k = 0; k < 30; k++) {
                    TelemetryRecord telemetry = new TelemetryRecord(
                            new TelemetryRecord.TelemetryId(Instant.now().plus(Duration.ofDays(rnd.nextLong(100))), sensor.getId()),
                            sensor,
                            rnd.nextDouble() * 100,
                            "QUALITY"
                    );
                    telemetryRepository.save(telemetry);
                }
            }
        }
    }

}
