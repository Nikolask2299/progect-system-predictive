package com.github.skrip.prediction.service;

import com.github.skrip.prediction.dto.TelemetryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryGenerate {

    @Value("${app.topic}")
    private String topic;

    @Value("${app.count}")
    private Integer count;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @EventListener(ApplicationReadyEvent.class)
    public void initTelemetryGenerate() {
        for (int i = 1; i <= count; i++) {
            var treadGen = new TelemetryThreadGen(i, 1000, 10);
            executor.execute(treadGen);
        }
    }


    @RequiredArgsConstructor
    private class TelemetryThreadGen implements Runnable {
        private final Integer sensorID;
        private final Integer iteration;
        private final Integer sizeBatch;

        private final Random rnd = new Random();
        private Double currVal = 0.0;

        @Override
        public void run() {
            for (int i = 0; i < iteration; i++) {
                 rnd.doubles(sizeBatch, -10.0, 10.0)
                        .mapToObj(this::createDTO)
                        .forEach(dto -> kafkaTemplate.send(topic, dto));
                 log.info("Thread {} send message DTO {}", Thread.currentThread().getName(), sizeBatch);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private TelemetryDTO createDTO(double val) {
            currVal += val;
            return new TelemetryDTO(sensorID, Instant.now(), currVal);
        }

    }

}
