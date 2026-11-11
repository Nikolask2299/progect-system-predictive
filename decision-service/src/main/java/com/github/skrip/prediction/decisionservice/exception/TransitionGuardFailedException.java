package com.github.skrip.prediction.decisionservice.exception;

/**
 * Исключение бросается когда guard (условие) перехода не выполнен.
 * Например: CONFIRM для алерта без назначенного инженера.
 */
public class TransitionGuardFailedException extends RuntimeException {

    public TransitionGuardFailedException(String reason) {
        super("Условие перехода не выполнено: " + reason);
    }
}
