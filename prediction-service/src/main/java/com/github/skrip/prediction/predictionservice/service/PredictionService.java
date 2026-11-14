package com.github.skrip.prediction.predictionservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.skrip.prediction.predictionservice.exception.PredictionError;
import com.github.skrip.prediction.predictionservice.exception.PredictionErrorRequest;
import com.github.skrip.prediction.predictionservice.model.dto.PredictionAggregationDto;
import com.github.skrip.prediction.predictionservice.model.dto.TelemetryAggregationDto;
import com.github.skrip.prediction.predictionservice.grpc.client.PredictionClient;
import com.github.skrip.prediction.predictionservice.model.entity.DevicePredictionAggregation;
import com.github.skrip.prediction.predictionservice.model.entity.PredictionAggregation;
import com.github.skrip.prediction.predictionservice.model.entity.TelemetryAggregation;
import com.github.skrip.prediction.predictionservice.model.enums.PredictionSource;
import com.github.skrip.prediction.predictionservice.repository.PredictionAggregationRepository;
import com.github.skrip.prediction.predictionservice.repository.TelemetryAggregationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class PredictionService {
    private static final String GET_DEVICE_ID_SQL = "SELECT id, device_id FROM prediction_schema.sensors";
    private static final String GET_SENSOR_ID_SQL = "SELECT id FROM prediction_schema.sensors WHERE device_id = ?";

    private final CircuitBreaker circuitBreaker;
    private final PredictionClient predictionClient;
    private final PredictionAggregationRepository aggregationRepository;
    private final TelemetryAggregationRepository telemetryAggregationRepository;
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

    public PredictionAggregationDto getPredictionAggregationDeviceId(Integer deviceId) throws PredictionErrorRequest {

        List<TelemetryAggregation> telemetryAggregationDtos = telemetryAggregationBuffer.getOrDefault(deviceId, new ArrayBlockingQueue<>(1)).stream().toList();

        if (!circuitBreaker.allowRequest()) return getCachePredictionAggregationDeviceId(deviceId);

        if (telemetryAggregationDtos.isEmpty()) {
            List<Integer> sensorIds = jdbcTemplate.queryForList(GET_SENSOR_ID_SQL, Integer.class, deviceId);
            telemetryAggregationDtos = telemetryAggregationRepository.findById_SensorIdInAndId_TimestampBetween(sensorIds, Instant.now().minusSeconds(60), Instant.now());
        }

        if (telemetryAggregationDtos.isEmpty()) {
            return getCachePredictionAggregationDeviceId(deviceId);
        }

        log.info("Received {} telemetry aggregations for deviceId: {}", telemetryAggregationDtos.size(), deviceId);

        var devicePredictionAggregation = getDevicePredictionAggregation(deviceId, telemetryAggregationDtos);
        var predictionAggregation = predictionClient.getPrediction(devicePredictionAggregation);
        if (predictionAggregation == null) {
            return getCachePredictionAggregationDeviceId(deviceId);
        }

        log.info("Prediction for deviceId: {} is {}", deviceId, predictionAggregation.getCalculateAt());
        cache.put(deviceId, predictionAggregation);

        var predictionAggregationDto = predictionAggregation.aggregationDto();
        predictionAggregation.setSource(PredictionSource.DATABASE);

        aggregationRepository.save(predictionAggregation);
        telemetryAggregationBuffer.remove(deviceId);

        return predictionAggregationDto;
    }

    private DevicePredictionAggregation getDevicePredictionAggregation(Integer deviceId, List<TelemetryAggregation> telemetryAggregation) {
        var devicePredictionAggregation = new DevicePredictionAggregation();
        devicePredictionAggregation.setDeviceId(deviceId);
        devicePredictionAggregation.setPredictions(telemetryAggregation);
        devicePredictionAggregation.setWindowStart(telemetryAggregation.getFirst().getId().timestamp());
        devicePredictionAggregation.setWindowEnd(telemetryAggregation.getLast().getId().timestamp());
        return devicePredictionAggregation;
    }

    private PredictionAggregationDto getCachePredictionAggregationDeviceId(Integer deviceId) {
        if (cache.getIfPresent(deviceId) == null) {
            try {
                var predictionAggregation = aggregationRepository.findFirstByDeviceIdOrderByCalculateAtDesc(deviceId);
                var predictionAggregationDto = predictionAggregation.orElseThrow();
                predictionAggregationDto.setSource(PredictionSource.DATABASE);
                predictionAggregationDto.setConfidence(predictionAggregationDto.getConfidence() * 0.7);
                log.info("Prediction for deviceId: {} is {}", deviceId, predictionAggregationDto.getCalculateAt());
                return predictionAggregationDto.aggregationDto();
            } catch (Exception e) {
                throw new PredictionError("No predictions not found for deviceId: " + deviceId + " at " + Instant.now() + " UTC");
            }
        }

        PredictionAggregation predictionAggregation = cache.getIfPresent(deviceId);
        Objects.requireNonNull(predictionAggregation).setConfidence(predictionAggregation.getConfidence() * 0.7);
        predictionAggregation.setSource(PredictionSource.CACHED);
        log.info("Prediction for deviceId: {} is {}", deviceId, predictionAggregation.getCalculateAt());
        return predictionAggregation.aggregationDto();
    }
}

