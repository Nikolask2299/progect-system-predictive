package com.github.skrip.prediction.decisionservice.model.repository;

import com.github.skrip.prediction.decisionservice.model.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {

    List<AlertHistory> findByAlertIdOrderByCreatedAtDesc(Long alertId);
}
