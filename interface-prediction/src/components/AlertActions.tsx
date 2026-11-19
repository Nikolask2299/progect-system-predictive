import {
  confirmAlert,
  startWork,
  resolveAlert,
  markFalsePositive,
} from "../api/alert";
import type { AlertDto } from "../api/types";

interface AlertActionsProps {
  alert: AlertDto;
  onAction: () => void;
  role: "OPERATOR" | "ENGINEER";
}

/**
 * Кнопки действий для алертов.
 * Видны только для допустимых переходов согласно автомату:
 *
 * NEW → OPERATOR: «Подтвердить» → ACKNOWLEDGED
 * ACKNOWLEDGED → ENGINEER: «Начать работы» → IN_PROGRESS
 * IN_PROGRESS → ENGINEER: «Решить» → RESOLVED
 * IN_PROGRESS → ENGINEER: «Ложное срабатывание» → FALSE_POSITIVE
 *
 * Терминальные состояния (RESOLVED, FALSE_POSITIVE, EXPIRED) → кнопки не показываются
 *
 * ВАЖНО: это только UX-разделение. Реальная проверка прав — на сервере.
 */
export default function AlertActions({ alert, onAction, role }: AlertActionsProps) {
  const isOperator = role === "OPERATOR";
  const isEngineer = role === "ENGINEER";
  const status = alert.status;

  // Терминальные состояния — никаких кнопок
  if (
    status === "RESOLVED" ||
    status === "FALSE_POSITIVE" ||
    status === "EXPIRED"
  ) {
    return <span className="no-actions">—</span>;
  }

  const handleConfirm = async () => {
    try {
      await confirmAlert(alert.id);
      onAction();
    } catch (err) {
      window.alert(err instanceof Error ? err.message : "Ошибка при подтверждении");
    }
  };

  const handleStartWork = async () => {
    try {
      await startWork(alert.id);
      onAction();
    } catch (err) {
      window.alert(err instanceof Error ? err.message : "Ошибка при начале работ");
    }
  };

  const handleResolve = async () => {
    try {
      await resolveAlert(alert.id);
      onAction();
    } catch (err) {
      window.alert(err instanceof Error ? err.message : "Ошибка при решении");
    }
  };

  const handleFalsePositive = async () => {
    try {
      await markFalsePositive(alert.id);
      onAction();
    } catch (err) {
      window.alert(err instanceof Error ? err.message : "Ошибка при отметке как ложное");
    }
  };

  // OPERATOR: только «Подтвердить» для NEW
  if (isOperator && status === "NEW") {
    return (
      <button className="btn btn-sm btn-primary" onClick={handleConfirm}>
        Подтвердить
      </button>
    );
  }

  // ENGINEER: полный набор действий
  if (isEngineer) {
    switch (status) {
      case "NEW":
        return (
          <button className="btn btn-sm btn-primary" onClick={handleConfirm}>
            Подтвердить
          </button>
        );
      case "ACKNOWLEDGED":
        return (
          <button className="btn btn-sm btn-secondary" onClick={handleStartWork}>
            Начать работы
          </button>
        );
      case "IN_PROGRESS":
        return (
          <div className="alert-action-buttons">
            <button className="btn btn-sm btn-success" onClick={handleResolve}>
              Решить
            </button>
            <button
              className="btn btn-sm btn-danger"
              onClick={handleFalsePositive}
            >
              Ложное срабатывание
            </button>
          </div>
        );
      default:
        return <span className="no-actions">—</span>;
    }
  }

  return <span className="no-actions">—</span>;
}
