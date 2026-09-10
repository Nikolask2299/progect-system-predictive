package com.project.pdm.ingestion.ingestion_service.config.properties;

import com.project.pdm.ingestion.ingestion_service.model.dto.TelemetryDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

@Configuration
public class KafkaConsumerConfig { }
