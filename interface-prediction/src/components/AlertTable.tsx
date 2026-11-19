import type { AlertDto } from "../api/types";
import { useRole } from "../context/RoleContext";
import AlertActions from "./AlertActions";

interface AlertTableProps {
  alerts: AlertDto[];
  onAction: () => void;
  role?: "OPERATOR" | "ENGINEER";
}

/**
 * Таблица алертов с колонками:
 * время, устройство, критичность, статус, сообщение, действия.
 */
export default function AlertTable({ alerts, onAction }: AlertTableProps) {
  if (alerts.length === 0) {
    return <p className="empty-state">Нет алертов, соответствующих фильтрам</p>;
  }

  return (
    <div className="alert-table-container">
      <table className="alert-table">
        <thead>
          <tr>
            <th>Время</th>
            <th>Устройство</th>
            <th>Критичность</th>
            <th>Статус</th>
            <th>Сообщение</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {alerts.map((alert) => (
            <AlertRow key={alert.id} alert={alert} onAction={onAction} />
          ))}
        </tbody>
      </table>
    </div>
  );
}

function AlertRow({ alert, onAction }: { alert: AlertDto; onAction: () => void }) {
  const { role } = useRole();

  return (
    <tr className={`alert-row alert-${alert.severity.toLowerCase()}`}>
      <td className="alert-time">
        {new Date(alert.createdAt).toLocaleString("ru-RU")}
      </td>
      <td className="alert-device">#{alert.deviceId}</td>
      <td>
        <span className={`alert-severity-badge severity-${alert.severity.toLowerCase()}`}>
          {alert.severity}
        </span>
      </td>
      <td className="alert-status">{alert.status}</td>
      <td className="alert-message">{alert.message}</td>
      <td className="alert-actions-cell">
        <AlertActions alert={alert} onAction={onAction} role={role} />
      </td>
    </tr>
  );
}
