import { useNavigate } from "react-router-dom";
import { useState, useEffect, useCallback } from "react";
import { useRole } from "../context/RoleContext";
import { useAlertEvents } from "../hooks/useWebSocket";
import { fetchDeviceInfoList, fetchPrediction } from "../api/prediction";
import type { DeviceDto, PredictionAggregationDto, AlertCreatedEvent } from "../api/types";
import AssetCard from "../components/AssetCard";

interface DeviceWithPrediction extends DeviceDto {
  prediction?: PredictionAggregationDto | null;
  color: "critical" | "warning" | "ok";
}

export default function Dashboard() {
  const navigate = useNavigate();
  const { role: _role } = useRole();
  const [devices, setDevices] = useState<DeviceWithPrediction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    fetchDeviceInfoList()
      .then(async (deviceList) => {
        if (cancelled) return;

        const enriched: DeviceWithPrediction[] = [];
        for (const device of deviceList) {
          try {
            const prediction = await fetchPrediction(device.id);
            enriched.push({
              ...device,
              prediction,
              color: getColorFromPrediction(prediction),
            });
          } catch {
            // No prediction available — show default green
            enriched.push({ ...device, prediction: null, color: "ok" });
          }
        }
        setDevices(enriched);
        setLoading(false);
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : "Ошибка загрузки");
          setLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, []);

  // WebSocket-обработка новых алертов — обновление дашборда
  const handleNewAlert = useCallback((event: AlertCreatedEvent) => {
    // Найти и обновить соответствующий актив
    setDevices((prev) =>
      prev.map((device) =>
        device.id === event.deviceId
          ? { ...device, color: getColorFromEvent(event) }
          : device
      )
    );
  }, []);

  useAlertEvents(handleNewAlert);

  const handleDeviceClick = (deviceId: number) => {
    navigate(`/assets/${deviceId}`);
  };

  if (loading) {
    return (
      <div className="page-loading">
        <div className="spinner" />
        <p>Загрузка дашборда...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-error">
        <p className="error-message">⚠ {error}</p>
        <p className="error-hint">Проверьте соединение с сервером</p>
      </div>
    );
  }

  return (
    <div className="dashboard-page">
      <h1>Дашборд активов</h1>
      <div className="asset-grid">
        {devices.map((device) => (
          <AssetCard
            key={device.id}
            device={device}
            onClick={() => handleDeviceClick(device.id)}
          />
        ))}
        {devices.length === 0 && (
          <div className="empty-state">Нет доступных активов</div>
        )}
      </div>
    </div>
  );
}

// ======================== Helpers ========================

function getColorFromPrediction(
  prediction: PredictionAggregationDto
): "critical" | "warning" | "ok" {
  if (prediction.riskScore > 0.8) return "critical";
  if (prediction.riskScore > 0.5) return "warning";
  return "ok";
}

function getColorFromEvent(event: AlertCreatedEvent): "critical" | "warning" | "ok" {
  if (event.severity === "CRITICAL") return "critical";
  if (event.severity === "WARNING") return "warning";
  return "ok";
}
