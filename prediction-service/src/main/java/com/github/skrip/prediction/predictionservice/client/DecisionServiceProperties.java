package com.github.skrip.prediction.predictionservice.client;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@ConfigurationProperties("app.client.decision-service")
public class DecisionServiceProperties {
    private String url;
}
