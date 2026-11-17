package com.github.skrip.prediction.decisionservice.model;

import com.github.skrip.prediction.decisionservice.model.entity.Alert;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Описание одного перехода конечного автомата.
 * @param targetStatus целевой статус,
 * @param guard условие перехода,
 * @param action действие.
 */
public record Transition(
    AlertStatus targetStatus,
    Predicate<Alert> guard,
    Consumer<Alert> action
) {}
