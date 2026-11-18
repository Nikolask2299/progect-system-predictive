package com.github.skrip.prediction.predictionservice.repository;

import com.github.skrip.prediction.predictionservice.model.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Integer> {
}
