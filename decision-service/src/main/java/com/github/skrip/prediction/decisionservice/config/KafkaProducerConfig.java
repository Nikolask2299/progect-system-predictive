package com.github.skrip.prediction.decisionservice.config;

import com.github.skrip.prediction.decisionservice.model.event.AlertCreatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka Producer для публикации событий алертов.
 */
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, AlertCreatedEvent> alertProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(org.springframework.kafka.core.ProducerFactory.VALUE_SERIALIZER_KEY,
            org.springframework.kafka.support.serializer.JsonSerializer.class);
        config.put(org.springframework.kafka.core.ProducerFactory.KEY_SERIALIZER_KEY,
            org.apache.kafka.common.serialization.StringSerializer.class);
        // Доверенные пакеты для десериализации
        config.put("spring.json.trusted.packages", "com.github.skrip.prediction.decisionservice.*");
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, AlertCreatedEvent> alertKafkaTemplate(
            ProducerFactory<String, AlertCreatedEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
