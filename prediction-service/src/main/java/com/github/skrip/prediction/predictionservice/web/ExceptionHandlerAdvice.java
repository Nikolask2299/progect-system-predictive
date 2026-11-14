package com.github.skrip.prediction.predictionservice.web;

import com.github.skrip.prediction.predictionservice.exception.PredictionError;
import com.github.skrip.prediction.predictionservice.exception.PredictionErrorRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(PredictionError.class)
    public ResponseEntity<PredictionErrorRequest> responseHandlerException(Exception e) {
        var errorResponse = new PredictionErrorRequest(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        log.error("Exception: {}", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
