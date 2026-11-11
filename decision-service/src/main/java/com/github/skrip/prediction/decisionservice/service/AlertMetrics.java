package com.github.skrip.prediction.decisionservice.service;

import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class AlertMetrics {

    private final MeterRegistry meterRegistry;

    public AlertMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordCreated() {
        Counter.builder("alerts.created.total")
            .description("Количество созданных новых алертов")
            .register(meterRegistry)
            .increment();
    }

    public void recordDeduplicated() {
        Counter.builder("alerts.deduplicated.total")
            .description("Количество дедуплицированных срабатываний")
            .register(meterRegistry)
            .increment();
    }

    public void recordFalsePositive() {
        Counter.builder("alerts.false_positive.total")
            .description("Количество ложных срабатываний")
            .register(meterRegistry)
            .increment();
    }

    public void recordTransition(AlertStatus toStatus) {
        Counter.builder("alerts.transitions.total")
            .description("Количество переходов состояний алертов")
            .tag("to", toStatus.name())
            .register(meterRegistry)
            .increment();
    }
}
