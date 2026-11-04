package com.github.skrip.prediction.predictionservice;

import com.github.skrip.prediction.predictionservice.grpc.PredictionServiceGrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootApplication
@ImportGrpcClients(target = "prediction-service", types = PredictionServiceGrpc.PredictionServiceBlockingStub.class)
public class PredictionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictionServiceApplication.class, args);
    }

}
