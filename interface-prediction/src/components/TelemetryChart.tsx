import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import type { TelemetryAggregationDto } from "../api/types";

interface TelemetryChartProps {
  data: TelemetryAggregationDto[];
}

/**
 * Линейный график телеметрии.
 * Ось X — timestamp, ось Y — mean.
 * Строится по агрегированным данным (TelemetryAggregationDto).
 */
export default function TelemetryChart({ data }: TelemetryChartProps) {
  if (data.length === 0) {
    return <p className="empty-state">Нет данных телеметрии</p>;
  }

  // Преобразуем данные для Recharts
  const chartData = data.map((item) => ({
    time: new Date(item.id.timestamp).toLocaleTimeString("ru-RU", {
      hour: "2-digit",
      minute: "2-digit",
    }),
    mean: item.mean,
    min: item.min,
    max: item.max,
    sensorId: item.id.sensorId,
  }));

  return (
    <div className="telemetry-chart-container">
      <ResponsiveContainer width="100%" height={350}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis
            dataKey="time"
            tick={{ fontSize: 12 }}
            interval="preserveStartEnd"
          />
          <YAxis tick={{ fontSize: 12 }} />
          <Tooltip
            labelFormatter={(label) => `Время: ${label}`}
            formatter={(value) => [
              (value as number).toFixed(2),
              "Значение",
            ]}
          />
          <Legend />
          <Line
            type="monotone"
            dataKey="mean"
            stroke="var(--accent)"
            name="Среднее"
            strokeWidth={2}
            dot={false}
          />
          <Line
            type="monotone"
            dataKey="min"
            stroke="#9ca3af"
            name="Мин"
            strokeWidth={1}
            strokeDasharray="5 5"
            dot={false}
          />
          <Line
            type="monotone"
            dataKey="max"
            stroke="#9ca3af"
            name="Макс"
            strokeWidth={1}
            strokeDasharray="5 5"
            dot={false}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
