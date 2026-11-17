package com.github.skrip.prediction.decisionservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
class DecisionServiceApplicationTests {

    //@Test
    void contextLoads() {
    }


    @Test
    void shouldCreateSingleAlertPerDeviceAndEventType() {
        decisionService.handleThresholdExceeding(
                DEVICE_ID, SENSOR_ID_VIBRATION,
                EVENT_TYPE_RUL_CRITICAL, 105.0);

        decisionService.handleThresholdExceeding(
                DEVICE_ID, SENSOR_ID_TEMPERATURE,
                EVENT_TYPE_RUL_CRITICAL, 108.0);

        assertThat(alertRepository
                .findByDeviceIdAndStatus(DEVICE_ID, NEW))
                .hasSize(1)
                .first()
                .extracting(Alert::getOccurrenceCount)
                .isEqualTo(2);
    }

    @Test
    void shouldTransitionThroughInProgressToResolved() {
        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        when(alertRepository.findById(1L))
                .thenReturn(Optional.of(alert));

        alertService.startMaintenance(1L, "engineer-1");
        assertThat(alert.getStatus())
                .isEqualTo(AlertStatus.IN_PROGRESS);

        alertService.resolve(1L, "engineer-1");
        assertThat(alert.getStatus())
                .isEqualTo(AlertStatus.RESOLVED);
        assertThat(alert.getResolvedAt()).isNotNull();
        
        verify(alertHistoryRepository)
                .recordTransition(alert, IN_PROGRESS,
                        RESOLVED, "engineer-1");
    }


}

2026-10-26 14:23:17 [WARN] AlertService : Illegal state transition attempt
        alert_id=1041, from=RESOLVED, to=ACKNOWLEDGED, user=engineer-1