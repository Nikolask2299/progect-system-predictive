package com.github.skrip.prediction.predictionservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DecisionRestClient {

    private final RestClient restClient;

    public DecisionRestClient(DecisionServiceProperties properties) {
        this.restClient = RestClient.builder()
                    .baseUrl(properties.getUrl())
                    .defaultHeader("Content-Type", "application/json")
                    .defaultHeader("Accept", "application/json")
                    .build();
    }

    public void postTelemetryToDecisionService(Integer deviceId, Integer sensorId, Double value) {
        restClient.post()
                  .uri("/telemetry")
                  .body(new TelemetryRecord(deviceId, sensorId, value))
                  .retrieve()
                  .toBodilessEntity();

    }

    public record TelemetryRecord(Integer deviceId, Integer sensorId, Double value) {}
}
