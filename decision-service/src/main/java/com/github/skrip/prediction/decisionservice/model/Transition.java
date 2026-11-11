package com.github.skrip.prediction.decisionservice.model;

import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Описание одного перехода конечного автомата.
 * targetStatus — целевой статус, guard — условие перехода, action — действие.
 */
public record Transition(
    AlertStatus targetStatus,
    Predicate<com.github.skrip.prediction.decisionservice.model.entity.Alert> guard,
    Consumer<com.github.skrip.prediction.decisionservice.model.entity.Alert> action
) {}
