package com.github.skrip.prediction.decisionservice.model.repository;

import com.github.skrip.prediction.decisionservice.model.entity.Alert;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import com.github.skrip.prediction.decisionservice.model.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long>, JpaSpecificationExecutor<Alert> {

    Optional<Alert> findByDeviceIdAndEventTypeAndStatusIn(Integer deviceId, EventType eventType, Collection<AlertStatus> status);

    @Query("SELECT a FROM Alert a WHERE a.status = 'NEW' AND a.createdAt < :cutoff")
    List<Alert> findExpiredNewAlerts(@Param("cutoff") Instant cutoff);
}
