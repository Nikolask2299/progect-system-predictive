package com.github.skrip.prediction.predictionservice.grpc.client;

import com.github.skrip.prediction.predictionservice.exception.PredictionError;
import com.github.skrip.prediction.predictionservice.grpc.PredictionRequest;
import com.github.skrip.prediction.predictionservice.grpc.PredictionResponse;
import com.github.skrip.prediction.predictionservice.model.entity.DevicePredictionAggregation;
import com.github.skrip.prediction.predictionservice.model.entity.PredictionAggregation;
import com.github.skrip.prediction.predictionservice.service.CircuitBreaker;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PredictionClient {

    private final CircuitBreaker circuitBreaker;

    @Value("${app.pdm.ml.timeout-ms}")
    private long timeoutMs;

    @Value("${app.pdm.ml.max-attempts}")
    private int maxAttempts;

    private final PredictionServiceBlockingStub predictionServiceBlockingStub;

    public PredictionAggregation getPrediction(DevicePredictionAggregation predictionAggregation) throws PredictionError {
        PredictionServiceBlockingStub blockingStub = predictionServiceBlockingStub
                .withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS);
        PredictionRequest request = toPredictionRequest(predictionAggregation);
        try {
            var res = blockingStub.getPrediction(request);
            circuitBreaker.recordSuccess();
            return toPredictionAggregation(res);
        } catch (StatusRuntimeException e) {
            var code = e.getStatus().getCode();
            circuitBreaker.recordFailure();

            return switch (code) {
                case DEADLINE_EXCEEDED -> {
                    log.warn("Deadline exceeded for request: {}", request, e);
                    try {
                        yield toPredictionAggregation(getPredictionWithRetry(request));
                    } catch (StatusRuntimeException e1) {
                        yield null;
                    }
                }
                case INTERNAL -> {
                    log.error("Internal error for request: {}", request, e);
                    throw new PredictionError(e.getMessage());
                }
                case UNAVAILABLE -> {
                    log.error("Service unavailable for request: {}", request, e);
                    try {
                        yield toPredictionAggregation(getPredictionWithRetry(request));
                    } catch (StatusRuntimeException e1) {
                        yield null;
                    }
                }
                case INVALID_ARGUMENT -> {
                    log.error("Invalid argument for request: {}", request, e);
                    throw new PredictionError(e.getMessage());
                }
                case CANCELLED -> {
                    log.warn("Request cancelled for request: {}", request, e);
                    throw new PredictionError(e.getMessage());
                }
                default -> {
                    log.error("Unexpected error for request: {} with code {}", request, code, e);
                    throw new PredictionError(e.getMessage());
                }
            };
        }
    }

    public PredictionResponse getPredictionWithRetry(PredictionRequest request) {
        StatusRuntimeException lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                PredictionServiceBlockingStub blockingStub = predictionServiceBlockingStub
                    .withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS);
                var response = blockingStub.getPrediction(request);
                circuitBreaker.recordSuccess();
                return response;
            } catch (StatusRuntimeException e) {
                lastException = e;
                circuitBreaker.recordFailure();
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
