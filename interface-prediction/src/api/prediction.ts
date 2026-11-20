import request from "./client";
import type {
  DeviceDto,
  PredictionAggregationDto,
  TelemetryAggregationDto,
} from "./types";

/**
 * GET /predictions/device-info-list
 * Список всех устройств.
 */
export function fetchDeviceInfoList(): Promise<DeviceDto[]> {
  return request<DeviceDto[]>("/prediction-service/api/v1/predictions/device-info-list");
}

/**
 * GET /predictions/{deviceId}
 * Прогноз по конкретному устройству.
 */
export function fetchPrediction(
  deviceId: number | string
): Promise<PredictionAggregationDto> {
  return request<PredictionAggregationDto>(
    `/prediction-service/api/v1/predictions/${deviceId}`
  );
}

/**
 * GET /predictions/telemetry/{deviceId}?from={from}&to={to}
 * Агрегированная история телеметрии за диапазон.
 */
export function fetchTelemetry(
  deviceId: number | string,
  from: string,
  to: string
): Promise<TelemetryAggregationDto[]> {
  return request<TelemetryAggregationDto[]>(
    `/prediction-service/api/v1/predictions/telemetry/${deviceId}?from=${from}&to=${to}`
  );
}
