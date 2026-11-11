package com.github.skrip.prediction.decisionservice.service;

import com.github.skrip.prediction.decisionservice.exception.IllegalTransitionException;
import com.github.skrip.prediction.decisionservice.exception.TransitionGuardFailedException;
import com.github.skrip.prediction.decisionservice.model.Transition;
import com.github.skrip.prediction.decisionservice.model.entity.Alert;
import com.github.skrip.prediction.decisionservice.model.enums.AlertEvent;
import com.github.skrip.prediction.decisionservice.model.enums.AlertStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

/**
 * Конечный автомат алертов на основе табличного подхода.
 * Соответствует диаграмме состояний рис. 3.4 отчёта НИР 2 семестр.
 * 
 * Переходы:
 *   NEW → ACKNOWLEDGED (CONFIRM, guard: assignedUserId != null)
 *   ACKNOWLEDGED → IN_PROGRESS (START_WORK)
 *   IN_PROGRESS → RESOLVED (RESOLVE)
 *   IN_PROGRESS → FALSE_POSITIVE (MARK_FALSE)
 *   NEW → EXPIRED (TIMEOUT)
 * 
 * Терминальные состояния: RESOLVED, FALSE_POSITIVE, EXPIRED (без исходящих переходов)
 */
@Component
public class AlertStateMachine {

    private final Map<AlertStatus, Map<AlertEvent, Transition>> transitions;

    public AlertStateMachine() {
        this.transitions = buildTransitionTable();
    }

    /**
     * Выполняет переход состояния алерта.
     * @throws IllegalTransitionException если переход не определён
     * @throws TransitionGuardFailedException если guard не пройден
     */
    public AlertStatus transition(Alert alert, AlertEvent event) {
        AlertStatus current = alert.getStatus();

        // 1. Проверить наличие перехода
        Map<AlertEvent, Transition> eventMap = transitions.get(current);
        if (eventMap == null || !eventMap.containsKey(event)) {
            throw new IllegalTransitionException(current, event);
        }

        Transition transition = eventMap.get(event);

        // 2. Проверить guard (охрану)
        if (!transition.guard().test(alert)) {
            throw new TransitionGuardFailedException(
                "Guard не пройден для %s → %s при событии %s".formatted(
                    current, transition.targetStatus(), event));
        }

        // 3. Выполнить действие
        transition.action().accept(alert);

        // 4. Вернуть новый статус
        return transition.targetStatus();
    }

    private Map<AlertStatus, Map<AlertEvent, Transition>> buildTransitionTable() {
        Map<AlertStatus, Map<AlertEvent, Transition>> table = new EnumMap<>(AlertStatus.class);

        // NEW → ACKNOWLEDGED (CONFIRM, guard: assignedUserId != null)
        addTransition(table, AlertStatus.NEW, AlertEvent.CONFIRM,
            AlertStatus.ACKNOWLEDGED,
            alert -> alert.getAssignedUserId() != null,
            alert -> alert.setAcknowledgedAt(Instant.now())
        );

        // ACKNOWLEDGED → IN_PROGRESS (START_WORK)
        addTransition(table, AlertStatus.ACKNOWLEDGED, AlertEvent.START_WORK,
            AlertStatus.IN_PROGRESS,
            alert -> true,
            alert -> {} // Заглушка: создание заказа в CMMS — позже
        );

        // IN_PROGRESS → RESOLVED (RESOLVE)
        addTransition(table, AlertStatus.IN_PROGRESS, AlertEvent.RESOLVE,
            AlertStatus.RESOLVED,
            alert -> true,
            alert -> alert.setResolvedAt(Instant.now())
        );

        // IN_PROGRESS → FALSE_POSITIVE (MARK_FALSE)
        addTransition(table, AlertStatus.IN_PROGRESS, AlertEvent.MARK_FALSE,
            AlertStatus.FALSE_POSITIVE,
            alert -> true,
            alert -> alert.setRelearningFlag(true) // Пометка для переобучения ML
        );

        // NEW → EXPIRED (TIMEOUT)
        addTransition(table, AlertStatus.NEW, AlertEvent.TIMEOUT,
            AlertStatus.EXPIRED,
            alert -> true,
            alert -> alert.setExpiredAt(Instant.now())
        );

        // Терминальные состояния: RESOLVED, FALSE_POSITIVE, EXPIRED
        // Не добавляем в таблицу — из них нельзя выйти

        return table;
    }

    private void addTransition(Map<AlertStatus, Map<AlertEvent, Transition>> table,
                               AlertStatus from, AlertEvent event,
                               AlertStatus to,
                               java.util.function.Predicate<Alert> guard,
                               java.util.function.Consumer<Alert> action) {
        table.computeIfAbsent(from, k -> new EnumMap<>(AlertEvent.class))
            .put(event, new Transition(to, guard, action));
    }
}
