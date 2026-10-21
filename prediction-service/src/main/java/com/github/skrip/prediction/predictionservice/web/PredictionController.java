package com.github.skrip.prediction.predictionservice.web;

import com.github.skrip.prediction.predictionservice.model.dto.PredictionAggregationDto;
import com.github.skrip.prediction.predictionservice.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping("/{deviceId}")
    public ResponseEntity<PredictionAggregationDto> getPrediction(@PathVariable Integer deviceId) {
        return ResponseEntity.ok(predictionService.getPredictionAggregationDeviceId(deviceId).aggregationDto());
    }
}
