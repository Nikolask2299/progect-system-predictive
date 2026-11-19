import { useParams, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { fetchDeviceInfoList, fetchPrediction, fetchTelemetry } from "../api/prediction";
import { fetchAlerts } from "../api/alert";
import type { DeviceDto, PredictionAggregationDto, TelemetryAggregationDto, AlertDto } from "../api/types";
import TelemetryChart from "../components/TelemetryChart";
import PredictionBlock from "../components/PredictionBlock";

export default function AssetDetails() {
  const { deviceId } = useParams<{ deviceId: string }>();
  const navigate = useNavigate();
  const [device, setDevice] = useState<DeviceDto | null>(null);
  const [prediction, setPrediction] = useState<PredictionAggregationDto | null>(null);
  const [telemetry, setTelemetry] = useState<TelemetryAggregationDto[]>([]);
  const [recentAlerts, setRecentAlerts] = useState<AlertDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!deviceId) return;

    let cancelled = false;
    setLoading(true);

    const loadDevice = async () => {
      try {
        // Загрузить список устройств для получения имени
        const devices = await fetchDeviceInfoList();
        const found = devices.find((d) => String(d.id) === deviceId);
        if (found && !cancelled) {
          setDevice(found);
        }

        // Загрузить прогноз
        const prediction = await fetchPrediction(deviceId);
        if (!cancelled) setPrediction(prediction);

        // Загрузить телеметрию за последние 7 дней
        const now = new Date();
        const from = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        const telemetryData = await fetchTelemetry(
          deviceId,
          from.toISOString(),
          now.toISOString()
        );
        if (!cancelled) setTelemetry(telemetryData);

        // Загрузить последние 5 алертов
        const alertsResponse = await fetchAlerts({
          deviceId: Number(deviceId),
          page: 0,
          size: 5,
          sort: "createdAt,desc",
        });
        if (!cancelled) setRecentAlerts(alertsResponse.content);
      } catch (err) {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : "Ошибка загрузки");
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    loadDevice();

    return () => {
      cancelled = true;
    };
  }, [deviceId]);

  if (loading) {
    return (
      <div className="page-loading">
        <div className="spinner" />
        <p>Загрузка данных актива...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-error">
        <p className="error-message">⚠ {error}</p>
        <button className="btn btn-secondary" onClick={() => navigate(-1)}>
          ← Назад
        </button>
      </div>
    );
  }

  return (
    <div className="asset-details-page">
      <div className="asset-details-header">
        <button className="btn btn-secondary" onClick={() => navigate(-1)}>
          ← Назад
        </button>
        <div>
          <h1>{device?.name || `#${deviceId}`}</h1>
          <p className="asset-type">{device?.type}</p>
        </div>
      </div>

      <div className="asset-details-grid">
        {/* Блок прогноза */}
        {prediction && <PredictionBlock prediction={prediction} />}

        {/* График телеметрии */}
        <section className="chart-section">
          <h2>Телеметрия</h2>
          <TelemetryChart data={telemetry} />
        </section>

        {/* Последние алерты */}
        <section className="recent-alerts-section">
          <h2>Последние алерты</h2>
          {recentAlerts.length === 0 ? (
            <p className="empty-state">Нет алертов</p>
          ) : (
            <div className="alerts-list">
              {recentAlerts.map((alert) => (
                <div key={alert.id} className={`alert-item alert-${alert.severity.toLowerCase()}`}>
                  <span className={`alert-severity-badge severity-${alert.severity.toLowerCase()}`}>
                    {alert.severity}
                  </span>
                  <div className="alert-item-content">
                    <p className="alert-item-message">{alert.message}</p>
                    <span className="alert-item-time">
                      {new Date(alert.createdAt).toLocaleString("ru-RU")}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  );
}
