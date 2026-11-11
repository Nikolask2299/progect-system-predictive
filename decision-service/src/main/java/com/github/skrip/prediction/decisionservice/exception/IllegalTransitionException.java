package com.github.skrip.prediction.decisionservice.exception;

import com.github.skrip.prediction.decisionservice.model.enums.AlertEvent;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;

/**
 * Исключение бросается при попытке нелегального перехода состояния.
 * Например: CONFIRM для алерта со статусом RESOLVED.
 */
public class IllegalTransitionException extends RuntimeException {

    public IllegalTransitionException(AlertStatus from, AlertEvent event) {
        super("Переход из %s по событию %s недопустим".formatted(from, event));
    }
}
