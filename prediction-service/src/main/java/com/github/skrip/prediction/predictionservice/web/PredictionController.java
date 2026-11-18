package com.github.skrip.prediction.predictionservice.web;

import com.github.skrip.prediction.predictionservice.exception.PredictionError;
import com.github.skrip.prediction.predictionservice.model.dto.DeviceDto;
import com.github.skrip.prediction.predictionservice.model.dto.PredictionAggregationDto;
import com.github.skrip.prediction.predictionservice.model.dto.TelemetryAggregationDto;
import com.github.skrip.prediction.predictionservice.model.entity.Device;
import com.github.skrip.prediction.predictionservice.model.entity.TelemetryAggregation;
import com.github.skrip.prediction.predictionservice.repository.DeviceRepository;
import com.github.skrip.prediction.predictionservice.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;
    private final DeviceRepository deviceRepository;

    @GetMapping("/{deviceId}")
    public ResponseEntity<PredictionAggregationDto> getPrediction(@PathVariable Integer deviceId) throws PredictionError {
        return ResponseEntity.ok(predictionService.getPredictionAggregationDeviceId(deviceId));
    }

    @GetMapping("/device-info-list")
    public ResponseEntity<List<DeviceDto>> getDeviceInfoList() {
        var deviceInfoList = deviceRepository.findAll().stream().map(Device::toDto).toList();
        return ResponseEntity.ok(deviceInfoList);
    }

    @GetMapping("/telemetry/{deviceId}")
    public ResponseEntity<List<TelemetryAggregationDto>> getTelemetry(
            @PathVariable(required = true) Integer deviceId,
            @RequestParam(required = true) Instant from,
            @RequestParam(required = true) Instant to) {
        var telemetryAggregation = predictionService.getTelemetryAggregationForDeviceId(deviceId, from, to)
                .stream().map(TelemetryAggregation::toDto).toList();
        return ResponseEntity.ok(telemetryAggregation);
    }

}
