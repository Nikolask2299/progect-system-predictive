package com.github.skrip.prediction.predictionservice.configuration;

import com.github.skrip.prediction.predictionservice.grpc.PredictionServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfiguration {

    @Bean
    PredictionServiceGrpc.PredictionServiceBlockingStub predictionServiceBlockingStub(GrpcChannelFactory channelFactory) {
        return PredictionServiceGrpc.newBlockingStub(channelFactory.createChannel("prediction-service"));
    }

}
