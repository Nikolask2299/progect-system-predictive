package com.github.skrip.prediction.predictionservice.model.dto;

import java.time.Instant;

public record DeviceDto(
        Integer id,
        String name,
        String type,
        String locationId,
        Instant installDate
){}
