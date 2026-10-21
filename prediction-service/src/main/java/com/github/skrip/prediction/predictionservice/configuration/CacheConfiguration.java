package com.github.skrip.prediction.predictionservice.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.skrip.prediction.predictionservice.model.entity.PredictionAggregation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CacheConfiguration {

    private final PropertiesCache propertiesCache;

    @Bean
    public Cache<Integer, PredictionAggregation> predictionCache() {
        return Caffeine.newBuilder()
                       .maximumSize(propertiesCache.getMaximumSize())
                       .expireAfterAccess(propertiesCache.getExpireAfterAccess(), java.util.concurrent.TimeUnit.SECONDS)
                       .build();
    }
}
