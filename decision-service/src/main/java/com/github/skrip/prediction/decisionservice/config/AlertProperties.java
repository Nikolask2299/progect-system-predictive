package com.github.skrip.prediction.decisionservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация параметров алертинга.
 * Все пороги и интервалы вынесены в application.yml.
 */
@Configuration
@ConfigurationProperties(prefix = "pdm.alert")
public class AlertProperties {

    /** Порог превышения для создания алерта (по умолчанию 100.0) */
    private double thresholdCritical = 100.0;

    /** Порог в часах для истечения алерта (по умолчанию 24) */
    private int expirationThresholdHours = 24;

    /** Интервал сканирования в мс (по умолчанию 3600000 = 1 час) */
    private long expirationScanIntervalMs = 3600000;

    public double getThresholdCritical() {
        return thresholdCritical;
    }

    public void setThresholdCritical(double thresholdCritical) {
        this.thresholdCritical = thresholdCritical;
    }

    public int getExpirationThresholdHours() {
        return expirationThresholdHours;
    }

    public void setExpirationThresholdHours(int expirationThresholdHours) {
        this.expirationThresholdHours = expirationThresholdHours;
    }

    public long getExpirationScanIntervalMs() {
        return expirationScanIntervalMs;
    }

    public void setExpirationScanIntervalMs(long expirationScanIntervalMs) {
        this.expirationScanIntervalMs = expirationScanIntervalMs;
    }
}
