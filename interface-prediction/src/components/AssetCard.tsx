import type { DeviceDto, PredictionAggregationDto } from "../api/types";

interface DeviceWithPrediction extends DeviceDto {
  prediction?: PredictionAggregationDto | null;
  color: "critical" | "warning" | "ok";
}

interface AssetCardProps {
  device: DeviceWithPrediction;
  onClick: () => void;
}

export default function AssetCard({ device, onClick }: AssetCardProps) {
  const { name, type, prediction, color } = device;

  return (
    <div className={`asset-card card-${color}`} onClick={onClick}>
      <div className={`asset-card-indicator indicator-${color}`} />
      <div className="asset-card-content">
        <h3 className="asset-card-name">{name}</h3>
        <p className="asset-card-type">{type}</p>
        {prediction && (
          <>
            <div className="asset-card-metrics">
              <div className="metric">
                <span className="metric-label">RUL</span>
                <span className="metric-value">{prediction.rulHours} ч</span>
              </div>
              <div className="metric">
                <span className="metric-label">Риск</span>
                <span className="metric-value">{Math.round(prediction.riskScore * 100)}%</span>
              </div>
            </div>
            {prediction.source === "CACHED" && (
              <span className="cache-badge">из кэша</span>
            )}
          </>
        )}
      </div>
    </div>
  );
}
