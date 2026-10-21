package com.github.skrip.prediction.predictionservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

@Builder
public class PredictionErrorResponse implements ErrorResponse {
    private HttpStatusCode statusCode;
    private ProblemDetail problemDetail;

    @Override
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public ProblemDetail getBody() {
        return problemDetail;
    }
}
