package com.github.skrip.prediction.decisionservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "pdm.alert")
public class AlertProperties {
    private double thresholdCritical = 100.0;
    private int expirationThresholdHours = 24;
    private long expirationScanIntervalMs = 3600000;
}
