import { useEffect } from "react";
import type { AlertCreatedEvent } from "../api/types";

interface AlertToastProps {
  event: AlertCreatedEvent;
  onDismiss: () => void;
}

/**
 * Всплывающее уведомление о новом алерте.
 * Автоматически исчезает через 5 секунд.
 */
export default function AlertToast({ event, onDismiss }: AlertToastProps) {
  useEffect(() => {
    const timer = setTimeout(onDismiss, 5000);
    return () => clearTimeout(timer);
  }, [onDismiss]);

  const severityLabel =
    event.severity === "CRITICAL"
      ? "КРИТИЧЕСКИЙ"
      : event.severity === "WARNING"
        ? "ВНИМАНИЕ"
        : "ИНФО";

  return (
    <div className={`alert-toast toast-${event.severity.toLowerCase()}`}>
      <div className="toast-content">
        <span className={`toast-badge badge-${event.severity.toLowerCase()}`}>
          {severityLabel}
        </span>
        <p className="toast-message">{event.message}</p>
        <span className="toast-device">
          Устройство: #{event.deviceId}
        </span>
      </div>
      <button className="toast-close" onClick={onDismiss}>
        ×
      </button>
    </div>
  );
}
