package com.github.skrip.prediction.predictionservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class PredictionErrorRequest extends Exception {
    private HttpStatus status;

    public PredictionErrorRequest(String message) {super(message);}

    public PredictionErrorRequest(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
