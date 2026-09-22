package com.github.skrip.prediction.predictionservice.exception;

public class PredictionErrorRequest extends RuntimeException {
    public PredictionErrorRequest(String message) {
        super(message);
    }
}
