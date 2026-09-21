package com.github.skrip.prediction.predictionservice.service;

import com.github.skrip.prediction.predictionservice.model.dto.TelemetryAggregationDto;
import com.github.skrip.prediction.predictionservice.grpc.client.PredictionClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionClient predictionClient;

    public void processMessage(TelemetryAggregationDto telemetryAggregationDto){


    }


}
