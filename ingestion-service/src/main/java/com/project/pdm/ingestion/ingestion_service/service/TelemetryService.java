package com.project.pdm.ingestion.ingestion_service.service;

import com.google.common.util.concurrent.AtomicDouble;
import com.project.pdm.ingestion.ingestion_service.model.dto.TelemetryDTO;
import com.project.pdm.ingestion.ingestion_service.model.entity.TelemetryAggregation;
import com.project.pdm.ingestion.ingestion_service.model.entity.TelemetryRecord;
import com.project.pdm.ingestion.ingestion_service.repository.TelemetryAggregationRepository;
import com.project.pdm.ingestion.ingestion_service.repository.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {
    private final String SQL_QUERY = "INSERT INTO telemetry (time, sensor_id, value, quality) VALUES (?, ?, ?, ?)";

    private ConcurrentHashMap<Integer, ArrayBlockingQueue<TelemetryRecord>> telemetryRecordBuffer = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, SensorWindowBuffer> aggregationMetricsBuffer = new ConcurrentHashMap<>();

    private final TelemetryRepository telemetryRepository;
    private final TelemetryAggregationRepository telemetryAggregationRepository;
    private final JdbcTemplate jdbcTemplate;

    public void saveProcessTelemetry(List<TelemetryDTO> telemetryRecords) {
        telemetryRecords.stream()
                .filter(Objects::nonNull)
                .map(TelemetryRecord::toEntity)
                .forEach( telemetryRecord -> {
                    telemetryRecordBuffer.computeIfAbsent(telemetryRecord.getId().sensorId(),
                            l -> new ArrayBlockingQueue<>(10000))
                            .offer(telemetryRecord);
                    aggregationMetricsBuffer.computeIfAbsent(telemetryRecord.getId().sensorId(),
                            l -> new SensorWindowBuffer())
                            .addValue(telemetryRecord.getValue());
                });
        log.info("Telemetry record saving in buffer: {}", telemetryRecords.size());
    }

    @Scheduled(fixedRate = 5000, initialDelay = 10000)
    private void processBufferTelemetry() {
        log.info("Process Buffer Telemetry Started");
        var bufferRecord = telemetryRecordBuffer;
        var bufferMetric = aggregationMetricsBuffer;
        this.telemetryRecordBuffer = new ConcurrentHashMap<>();
        this.aggregationMetricsBuffer = new ConcurrentHashMap<>();

        bufferRecord.forEach(5, (key, buffer) -> {
            try {
                bufferMetric.get(key).calculateStandardDeviation(buffer);
                TelemetryAggregation telemetryAggregation = bufferMetric.get(key).toEntityAggregation(key);
                telemetryAggregationRepository.save(telemetryAggregation);
                jdbcTemplate.batchUpdate(SQL_QUERY, buffer.stream().toList(), buffer.size(),
                        (ps, record) -> {
                            ps.setTimestamp(1, Timestamp.from(record.getId().timestamp()));
                            ps.setInt(2, record.getId().sensorId());
                            ps.setDouble(3, record.getValue());
                            ps.setString(4, record.getQuality());
                        });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static class SensorWindowBuffer {
        private final DoubleAdder summa = new DoubleAdder();
        private final AtomicLong counter = new AtomicLong();
        private final DoubleAccumulator max = new DoubleAccumulator(Math::max, Double.MIN_VALUE);
        private final DoubleAccumulator min = new DoubleAccumulator(Math::min, Double.MAX_VALUE);
        private final AtomicDouble stdDev = new AtomicDouble();
        private final AtomicDouble mean = new AtomicDouble();

        public void addValue(double value) {
            counter.incrementAndGet();
            summa.add(value);
            max.accumulate(value);
            min.accumulate(value);
        }

        public double getMeanAggregation() {
            if (counter.get() == 0) return 0;
            this.mean.set(summa.sum() / counter.get());
            return mean.get();
        }

        public Double calculateStandardDeviation(BlockingQueue<TelemetryRecord> telemetryRecords) throws Exception {
            getMeanAggregation();
            double mean = this.mean.get();
            DoubleAdder summSquareDiff = new DoubleAdder();
            if (this.counter.get() == 0 || telemetryRecords.isEmpty()) throw new Exception();
            if (this.counter.get() != telemetryRecords.size()) throw new Exception();
            telemetryRecords.stream().map(TelemetryRecord::getValue)
                    .forEach(val -> summSquareDiff.add(Math.pow((val - mean), 2.0)));

            this.stdDev.set(Math.sqrt(summSquareDiff.sum() / (telemetryRecords.size() - 1)));
            return stdDev.get();
        }

        public TelemetryAggregation toEntityAggregation(Integer sensorID) {
            return new TelemetryAggregation(
                    new TelemetryAggregation.TelemetryID(sensorID, Instant.now()),
                    this.counter.longValue(),
                    this.summa.sum(),
                    this.mean.get(),
                    this.max.get(),
                    this.min.get(),
                    this.stdDev.get()
            );
        }

    }

}
