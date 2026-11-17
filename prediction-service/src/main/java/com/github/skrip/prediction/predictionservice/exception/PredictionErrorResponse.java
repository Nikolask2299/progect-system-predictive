package com.github.skrip.prediction.predictionservice.exception;

import org.springframework.http.HttpStatus;

public record PredictionErrorResponse (
    HttpStatus status,
    String message
){}
