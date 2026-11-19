// PredictionSource - Источник прогноза
export type PredictionSource = "ACTUAL" | "CACHED" | "UNSPECIFIED" | "DATABASE";

// DeviceDto - Устройство
export type DeviceDto = {
  id: number;
  name: string;
  type: string;
  locationId: string;
  installDate: string;
};

// PredictionAggregationDto - Прогноз по устройству
export type PredictionAggregationDto = {
  id: number;
  deviceId: number;
  rulHours: number;
  confidence: number;
  riskScore: number;
  source: PredictionSource;
  calculateAt: string;
  maintenanceRecommendation: string;
};

// TelemetryID - Идентификатор телеметрии
export type TelemetryID = {
  sensorId: number;
  timestamp: string;
};

// TelemetryAggregationDto - Агрегированная телеметрия
export type TelemetryAggregationDto = {
  id: TelemetryID;
  count: number;
  sum: number;
  mean: number;
  max: number;
  min: number;
  stdDev: number;
};

// AlertStatus - Статус алерта
export type AlertStatus =
  | "NEW"
  | "ACKNOWLEDGED"
  | "RESOLVED"
  | "IN_PROGRESS"
  | "FALSE_POSITIVE"
  | "EXPIRED";

// AlertSeverity - Уровень важности
export type AlertSeverity = "INFO" | "WARNING" | "CRITICAL";

// EventType - Тип события
export type EventType =
  | "THRESHOLD_EXCEEDED"
  | "CONFIRM"
  | "RESOLVE"
  | "START"
  | "STOP"
  | "TIMEOUT"
  | "FALSE_POSITIVE";

// AlertDto - Базовый алерт
export type AlertDto = {
  id: number;
  deviceId: number;
  sensorId: number;
  status: AlertStatus;
  severity: AlertSeverity;
  eventType: EventType;
  message: string;
  occurrenceCount: number;
  createdAt: string;
  lastTriggeredAt: string;
  assignedUserId: number;
};

// AlertDetailsDto - Детали алерта с историей
export type AlertDetailsDto = AlertDto & {
  history: AlertHistoryEntryDto[];
};

// AlertHistoryEntryDto - Запись истории состояний
export type AlertHistoryEntryDto = {
  id: number;
  fromStatus: AlertStatus;
  toStatus: AlertStatus;
  event: EventType;
  actorUserId: number;
  comment: string;
  createdAt: string;
};

// AlertCreatedEvent - Событие нового алерта (WebSocket/Kafka)
export type AlertCreatedEvent = {
  alertId: number;
  deviceId: number;
  severity: AlertSeverity;
  eventType: EventType;
  timestamp: string;
  message: string;
};

// Paginated response
export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

// Alert filters for GET /alerts
export type AlertFilters = {
  status?: AlertStatus;
  deviceId?: number;
  severity?: AlertSeverity;
  fromDate?: string;
  toDate?: string;
  page?: number;
  size?: number;
  sort?: string;
};

export type Role = "OPERATOR" | "ENGINEER";
