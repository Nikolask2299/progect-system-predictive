package com.github.skrip.prediction.predictionservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CircuitBreaker {
    private AtomicInteger failureCount;
    private State state = State.CLOSED;
    private Instant lastFailureTime;

    @Value("${app.circuit.failure-threshold}")
    private Integer failureThreshold;

    @Value("${app.circuit.reset-timeout}")
    private Integer resentTimeout;

    public boolean allowRequest() {
        Instant resetTime = Instant.now().minusSeconds(resentTimeout);
        if (state == State.OPEN && lastFailureTime.isBefore(resetTime)) {
            state = State.HALF_OPEN;
        }
        return state == State.HALF_OPEN || state == State.CLOSED;
    }

    public void recordSuccess() {
        if (state == State.HALF_OPEN) {
            state = State.CLOSED;
            failureCount.set(0);
        }
    }

    public void recordFailure() {
        if (failureCount.incrementAndGet() >= failureThreshold) {
            state = State.OPEN;
        }
        lastFailureTime = Instant.now();
    }

    private enum State {
        OPEN,
        CLOSED,
        HALF_OPEN;
    }
}
