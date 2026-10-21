package com.github.skrip.prediction.predictionservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.cache")
@Getter
@Setter
public class PropertiesCache {
    private String cacheName;
    private Integer maximumSize;
    private Integer expireAfterAccess;
}
