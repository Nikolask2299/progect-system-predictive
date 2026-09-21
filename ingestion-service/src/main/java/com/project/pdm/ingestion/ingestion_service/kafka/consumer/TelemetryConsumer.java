package com.project.pdm.ingestion.ingestion_service.kafka.consumer;

import com.project.pdm.ingestion.ingestion_service.model.dto.TelemetryDTO;
import com.project.pdm.ingestion.ingestion_service.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryConsumer {

    private final TelemetryService telemetryService;

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${app.kafka.group-id}", concurrency = "4")
    public void telemetryConsumerListener(List<TelemetryDTO> dtoConsumerRecords, Acknowledgment acknowledgment) {
        log.info("Received telemetry message {} count.", dtoConsumerRecords.size());
        telemetryService.saveProcessTelemetry(dtoConsumerRecords);
        acknowledgment.acknowledge();
    }

}
