package com.github.skrip.prediction.predictionservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.skrip.prediction.predictionservice.model.dto.TelemetryAggregationDto;
import com.github.skrip.prediction.predictionservice.grpc.client.PredictionClient;
import com.github.skrip.prediction.predictionservice.model.entity.DevicePredictionAggregation;
import com.github.skrip.prediction.predictionservice.model.entity.PredictionAggregation;
import com.github.skrip.prediction.predictionservice.model.entity.TelemetryAggregation;
import com.github.skrip.prediction.predictionservice.model.enums.PredictionSource;
import com.github.skrip.prediction.predictionservice.repository.PredictionAggregationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class PredictionService {
    private static final String GET_DEVICE_ID_SQL = "SELECT id, device_id FROM prediction_schema.sensors";

    private final CircuitBreaker circuitBreaker;
    private final PredictionClient predictionClient;
    private final PredictionAggregationRepository aggregationRepository;
    private final Cache<Integer, PredictionAggregation> cache;
    private final ConcurrentHashMap<Integer, ArrayBlockingQueue<TelemetryAggregation>> telemetryAggregationBuffer = new ConcurrentHashMap<>();
    private final HashMap<Integer, Integer> sensorIdToDeviceIdMap = new HashMap<>();
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        jdbcTemplate.query(GET_DEVICE_ID_SQL, (rs, rowNum) ->
                sensorIdToDeviceIdMap.put(rs.getInt("id"), rs.getInt("device_id")));
        log.info("Loaded {} sensors from DB", sensorIdToDeviceIdMap.size());
    }

    public void processMessageTelemetryDto(TelemetryAggregationDto telemetryAggregationDto) {
        telemetryAggregationBuffer.computeIfAbsent(sensorIdToDeviceIdMap.get(telemetryAggregationDto.id().sensorId()),
                l -> new ArrayBlockingQueue<>(1000))
                .offer(TelemetryAggregation.toEntity(telemetryAggregationDto));
    }

    public PredictionAggregation getPredictionAggregationDeviceId(Integer deviceId) {

        ArrayBlockingQueue<TelemetryAggregation> telemetryAggregationDtos = telemetryAggregationBuffer.get(deviceId);

        if (telemetryAggregationDtos == null || telemetryAggregationDtos.isEmpty()) {
            return getCachePredictionAggregationDeviceId(deviceId);
        }

        if (!circuitBreaker.allowRequest()) return getCachePredictionAggregationDeviceId(deviceId);

        var devicePredictionAggregation = getDevicePredictionAggregation(deviceId, telemetryAggregationDtos);
        var predictionAggregation = predictionClient.getPrediction(devicePredictionAggregation);
        if (predictionAggregation == null) {
            return getCachePredictionAggregationDeviceId(deviceId);
        }

        aggregationRepository.save(predictionAggregation);
        telemetryAggregationBuffer.remove(deviceId);
        return predictionAggregation;
    }

    private DevicePredictionAggregation getDevicePredictionAggregation(Integer deviceId, ArrayBlockingQueue<TelemetryAggregation> telemetryAggregation) {
        var devicePredictionAggregation = new DevicePredictionAggregation();
        devicePredictionAggregation.setDeviceId(deviceId);
        var prediction = telemetryAggregation.stream().toList();
        devicePredictionAggregation.setPredictions(prediction);
        devicePredictionAggregation.setWindowStart(prediction.getFirst().getId().timestamp());
        devicePredictionAggregation.setWindowEnd(prediction.getLast().getId().timestamp());
        return devicePredictionAggregation;
    }

    private PredictionAggregation getCachePredictionAggregationDeviceId(Integer deviceId) {
        if (cache.getIfPresent(deviceId) == null) {
            return new PredictionAggregation();
        }
        PredictionAggregation predictionAggregation = cache.getIfPresent(deviceId);
        Objects.requireNonNull(predictionAggregation).setConfidence(predictionAggregation.getConfidence() * 0.7);
        predictionAggregation.setSource(PredictionSource.PREDICTION_SOURCE_CACHED);
        return predictionAggregation;
    }
}

