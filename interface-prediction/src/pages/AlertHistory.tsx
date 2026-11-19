import { useState, useEffect, useCallback } from "react";
import { useRole } from "../context/RoleContext";
import { useAlertEvents } from "../hooks/useWebSocket";
import { fetchAlerts } from "../api/alert";
import type { AlertDto, AlertFilters, AlertCreatedEvent } from "../api/types";
import AlertTable from "../components/AlertTable";

const STATUS_OPTIONS: { value: AlertFilters["status"]; label: string }[] = [
  { value: undefined, label: "Все" },
  { value: "NEW", label: "NEW" },
  { value: "ACKNOWLEDGED", label: "ACKNOWLEDGED" },
  { value: "IN_PROGRESS", label: "IN_PROGRESS" },
  { value: "RESOLVED", label: "RESOLVED" },
  { value: "FALSE_POSITIVE", label: "FALSE_POSITIVE" },
  { value: "EXPIRED", label: "EXPIRED" },
];

const SEVERITY_OPTIONS: { value: AlertFilters["severity"]; label: string }[] = [
  { value: undefined, label: "Все" },
  { value: "CRITICAL", label: "CRITICAL" },
  { value: "WARNING", label: "WARNING" },
  { value: "INFO", label: "INFO" },
];

export default function AlertHistory() {
  const { role } = useRole();
  const [alerts, setAlerts] = useState<AlertDto[]>([]);
  const [_totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Фильтры
  const [filters, setFilters] = useState<AlertFilters>({
    page: 0,
    size: 10,
    sort: "createdAt,desc",
  });

  const loadAlerts = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchAlerts(filters);
      setAlerts(response.content);
      setTotalElements(response.totalElements);
      setTotalPages(response.totalPages);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Ошибка загрузки");
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    loadAlerts();
  }, [loadAlerts]);

  // Обновление при получении нового алерта через WebSocket
  const handleNewAlert = useCallback((_event: AlertCreatedEvent) => {
    loadAlerts();
  }, [loadAlerts]);

  useAlertEvents(handleNewAlert);

  const handleFilterChange = (key: keyof AlertFilters, value: unknown) => {
    setFilters((prev) => ({ ...prev, [key]: value, page: 0 }));
  };

  const handlePageChange = (page: number) => {
    setFilters((prev) => ({ ...prev, page }));
  };

  return (
    <div className="alert-history-page">
      <h1>История алертов</h1>

      {/* Фильтры */}
      <div className="alert-filters">
        <div className="filter-group">
          <label>Статус:</label>
          <select
            value={filters.status ?? ""}
            onChange={(e) =>
              handleFilterChange(
                "status",
                e.target.value === "" ? undefined : e.target.value
              )
            }
          >
            {STATUS_OPTIONS.map((opt) => (
              <option key={opt.value ?? "all"} value={opt.value ?? ""}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label>Критичность:</label>
          <select
            value={filters.severity ?? ""}
            onChange={(e) =>
              handleFilterChange(
                "severity",
                e.target.value === "" ? undefined : e.target.value
              )
            }
          >
            {SEVERITY_OPTIONS.map((opt) => (
              <option key={opt.value ?? "all"} value={opt.value ?? ""}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label>Страница:</label>
          <span>
            {filters.page ? filters.page + 1 : 1} / {totalPages || 1}
          </span>
        </div>
      </div>

      {/* Таблица */}
      {loading ? (
        <div className="page-loading">
          <div className="spinner" />
          <p>Загрузка...</p>
        </div>
      ) : error ? (
        <div className="page-error">
          <p className="error-message">⚠ {error}</p>
        </div>
      ) : (
        <>
          <AlertTable
            alerts={alerts}
            onAction={loadAlerts}
            role={role}
          />

          {/* Пагинация */}
          {totalPages > 1 && (
            <div className="pagination">
              <button
                className="btn btn-secondary"
                disabled={filters.page === 0}
                onClick={() => handlePageChange((filters.page ?? 0) - 1)}
              >
                ← Назад
              </button>
              <span className="pagination-info">
                Страница {filters.page ? filters.page + 1 : 1} из {totalPages}
              </span>
              <button
                className="btn btn-secondary"
                disabled={filters.page! + 1 >= totalPages}
                onClick={() => handlePageChange((filters.page ?? 0) + 1)}
              >
                Далее →
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
