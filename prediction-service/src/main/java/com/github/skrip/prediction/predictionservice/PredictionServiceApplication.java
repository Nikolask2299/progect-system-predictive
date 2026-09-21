package com.github.skrip.prediction.predictionservice;

import com.github.skrip.prediction.predictionservice.grpc.PredictionRequest;
import com.github.skrip.prediction.predictionservice.grpc.client.PredictionClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootApplication
@ImportGrpcClients(basePackages = "com.github.skrip.prediction.predictionservice.grpc.client")
public class PredictionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictionServiceApplication.class, args);
    }

}
