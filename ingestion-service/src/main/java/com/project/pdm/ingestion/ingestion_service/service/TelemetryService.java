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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final ConcurrentHashMap<Integer, ArrayBlockingQueue<TelemetryRecord>> telemetryRecordBuffer = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, SensorWindowBuffer> aggregationMetricsBuffer = new ConcurrentHashMap<>();

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

        public Double calculateStandardDeviation(BlockingDeque<TelemetryRecord> telemetryRecords) throws Exception {
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
