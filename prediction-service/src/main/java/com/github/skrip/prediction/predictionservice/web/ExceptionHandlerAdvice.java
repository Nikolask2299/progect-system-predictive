package com.github.skrip.prediction.predictionservice.web;

import com.github.skrip.prediction.predictionservice.model.dto.PredictionErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> responseHandlerException(Exception e) {
        var errorResponse = PredictionErrorResponse.builder();
        errorResponse.statusCode(HttpStatus.BAD_REQUEST);
        errorResponse.problemDetail(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage()));
        return ResponseEntity.badRequest().body(errorResponse.build());
    }
}
