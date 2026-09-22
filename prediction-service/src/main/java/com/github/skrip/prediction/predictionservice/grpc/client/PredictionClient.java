package com.github.skrip.prediction.predictionservice.grpc.client;

import com.github.skrip.prediction.predictionservice.exception.PredictionErrorRequest;
import com.github.skrip.prediction.predictionservice.grpc.PredictionRequest;
import com.github.skrip.prediction.predictionservice.grpc.PredictionResponse;
import com.github.skrip.prediction.predictionservice.model.entity.DevicePredictionAggregation;
import com.github.skrip.prediction.predictionservice.model.entity.PredictionAggregation;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.github.skrip.prediction.predictionservice.grpc.PredictionServiceGrpc.PredictionServiceBlockingStub;
import static com.github.skrip.prediction.predictionservice.utils.PredictionMapper.toPredictionAggregation;
import static com.github.skrip.prediction.predictionservice.utils.PredictionMapper.toPredictionRequest;

@Component
@Slf4j
public class PredictionClient {

    @Value("${app.pdm.ml.timeout-ms}")
    private long timeoutMs;

    @Value("${app.pdm.ml.max-attempts}")
    private int maxAttempts;

    PredictionServiceBlockingStub predictionServiceBlockingStub;

    public PredictionClient(PredictionServiceBlockingStub predictionServiceBlockingStub) {
        this.predictionServiceBlockingStub = predictionServiceBlockingStub;
    }

    public PredictionAggregation getPrediction(DevicePredictionAggregation predictionAggregation) {
        PredictionServiceBlockingStub blockingStub = predictionServiceBlockingStub
                .withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS);
        PredictionRequest request = toPredictionRequest(predictionAggregation);
        try {
            return toPredictionAggregation(blockingStub.getPrediction(request));
        } catch (StatusRuntimeException e) {
            var code = e.getStatus().getCode();
            return switch (code) {
                case DEADLINE_EXCEEDED -> {
                    log.warn("Deadline exceeded for request: {}", request.toString(), e);
                    yield toPredictionAggregation(getPredictionWithRetry(request));
                }
                case INTERNAL -> {
                    log.error("Internal error for request: {}", request.toString(), e);
                    throw new PredictionErrorRequest(e.getMessage());
                }
                case UNAVAILABLE -> {
                    log.error("Service unavailable for request: {}", request.toString(), e);
                    yield toPredictionAggregation(getPredictionWithRetry(request));
                }
                case INVALID_ARGUMENT -> {
                    log.error("Invalid argument for request: {}", request.toString(), e);
                    throw new PredictionErrorRequest(e.getMessage());
                }
                case CANCELLED -> {
                    log.warn("Request cancelled for request: {}", request.toString(), e);
                    throw new PredictionErrorRequest(e.getMessage());
                }
                default -> {
                    log.error("Unexpected error for request: {} with code {}", request.toString(), code, e);
                    throw new PredictionErrorRequest(e.getMessage());
                }
            };
        }
    }

    public PredictionResponse getPredictionWithRetry(PredictionRequest request) {
        PredictionServiceBlockingStub blockingStub = predictionServiceBlockingStub
                .withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS);

        StatusRuntimeException lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return blockingStub.getPrediction(request);
            } catch (StatusRuntimeException e) {
                lastException = e;

                if (!isRetryCode(e.getStatus().getCode())) throw e;

                log.warn("Retried attempt {}:{} failed {} from request {}",
                        attempt, maxAttempts, e.getStatus(), request.toString());

                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep((long) 1000 * attempt);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        throw Objects.requireNonNull(lastException);
    }

    private boolean isRetryCode(Status.Code code) {
        switch (code) {
            case UNAVAILABLE, DEADLINE_EXCEEDED, RESOURCE_EXHAUSTED -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
