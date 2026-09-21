package com.github.skrip.prediction.predictionservice.grpc.client;

import com.github.skrip.prediction.predictionservice.grpc.PredictionRequest;
import com.github.skrip.prediction.predictionservice.grpc.PredictionResponse;
import com.github.skrip.prediction.predictionservice.grpc.PredictionServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.github.skrip.prediction.predictionservice.grpc.PredictionServiceGrpc.PredictionServiceBlockingStub;

@Component
@Slf4j
public class PredictionClient {

    @Value("${app.pdm.ml.timeout-ms}")
    private long timeoutMs;

    PredictionServiceBlockingStub predictionServiceBlockingStub;

    public PredictionClient(PredictionServiceBlockingStub predictionServiceBlockingStub) {
        this.predictionServiceBlockingStub = predictionServiceBlockingStub;
    }

    public PredictionResponse getPrediction(PredictionRequest request) {
        PredictionServiceBlockingStub blockingStub = predictionServiceBlockingStub
                .withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS);
        try {
            return blockingStub.getPrediction(request);
        } catch (Exception e) {
            log.error("Error while getting prediction", e);
            return null;
        }
    }
}
