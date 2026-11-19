import type { PredictionAggregationDto, PredictionSource } from "../api/types";

interface PredictionBlockProps {
  prediction: PredictionAggregationDto;
}

/**
 * Блок прогноза: RUL в часах, риск в %, рекомендация,
 * пометка «из кэша» если source === "CACHED".
 */
export default function PredictionBlock({ prediction }: PredictionBlockProps) {
  const isCached = prediction.source === "CACHED";
  const riskPercent = Math.round(prediction.riskScore * 100);

  const riskColor =
    riskPercent > 80
      ? "critical"
      : riskPercent > 50
        ? "warning"
        : "ok";

  const sourceLabel: Record<PredictionSource, string> = {
    ACTUAL: "Модель (актуальный)",
    CACHED: "Кэш",
    UNSPECIFIED: "Не указан",
    DATABASE: "База данных",
  };

  return (
    <section className="prediction-block">
      <h2>Прогноз</h2>
      <div className="prediction-metrics">
        <div className="prediction-metric">
          <span className="metric-label">RUL</span>
          <span className="metric-value">{prediction.rulHours} ч</span>
        </div>
        <div className={`prediction-metric metric-risk-${riskColor}`}>
          <span className="metric-label">Риск</span>
          <span className="metric-value">{riskPercent}%</span>
        </div>
        <div className="prediction-metric">
          <span className="metric-label">Доверие</span>
          <span className="metric-value">{Math.round(prediction.confidence * 100)}%</span>
        </div>
      </div>
      <div className="prediction-recommendation">
        <span className="metric-label">Рекомендация:</span>
        <p>{prediction.maintenanceRecommendation}</p>
      </div>
      <div className="prediction-meta">
        <span className="source-label">{sourceLabel[prediction.source]}</span>
        {isCached && <span className="cache-badge">из кэша</span>}
        <span className="calc-time">
          Рассчитано: {new Date(prediction.calculateAt).toLocaleString("ru-RU")}
        </span>
      </div>
    </section>
  );
}
