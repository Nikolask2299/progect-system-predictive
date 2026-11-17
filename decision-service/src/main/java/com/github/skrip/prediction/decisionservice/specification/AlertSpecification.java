package com.github.skrip.prediction.decisionservice.specification;

import com.github.skrip.prediction.decisionservice.model.entity.Alert;
import com.github.skrip.prediction.decisionservice.model.enums.AlertSeverity;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification для динамической фильтрации алертов.
 * Используется в AlertController.listAlerts() для построения запросов.
 */
public class AlertSpecification {

    private AlertSpecification() {}

    public static Specification<Alert> withFilters(
            AlertStatus status,
            Integer deviceId,
            AlertSeverity severity,
            Instant fromDate,
            Instant toDate) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (deviceId != null) {
                predicates.add(cb.equal(root.get("deviceId"), deviceId));
            }
            if (severity != null) {
                predicates.add(cb.equal(root.get("severity"), severity));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
