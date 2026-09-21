package com.github.skrip.prediction.predictionservice.kafka.consumer;

import com.github.skrip.prediction.predictionservice.model.dto.TelemetryAggregationDto;
import com.github.skrip.prediction.predictionservice.service.PredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryAggregationConsumer {

    private final PredictionService predictionService;

    @KafkaListener(topics = "${app.kafka.topic-aggregation}", groupId = "${app.kafka.group-id}")
    public void listenTelemetryAggregation(TelemetryAggregationDto telemetryAggregationDto, Acknowledgment acknowledgment) {
        log.info("Received telemetry aggregation message.");
        predictionService.processMessage(telemetryAggregationDto);
        acknowledgment.acknowledge();
    }
}
