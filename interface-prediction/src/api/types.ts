
//DeviceDao - Устройства и их параметры
interface DeviceDto {
    id: number;
    name: string;
    type: string;
    locationId: string;
    installDate: string;
}

//PredictionSource - Источник прогноза
type PredictionSource = "ACTUAL" | "CACHED" | "UNSPECIFIED" | "DATABASE";

//PredictionDao - Прогноз и его параметры
interface PredictionAggregationDto {
    id: number;
    deviceId: number;
    rulHours: number;
    confidence: number;
    riskScore: number;
    source: PredictionSource;
    calculateAt: string;
    maintenanceRecommendation: string;
}

//TelemetryID - Идентификатор телеметрии
type TelemetryID = {
    sensorId: number;
    timestamp: string;
};

//TelemetryDao - Телеметрия и ее параметры
interface TelemetryAggregationDto {
    id: TelemetryID;
    count: number;
    sum: number;
    mean: number;
    max: number;
    min: number;
    stdDev: number;
}

//AlertStatus - Статус алерта
type AlertStatus = "NEW" | "ACKNOWLEDGED" | "RESOLVED" | "IN_PROGRESS" | "FALSE_POSITIVE" | "EXPIRED";

//AlertSeverity - Уровень важности алерта
type AlertSeverity = "INFO" | "WARNING" | "CRITICAL";

//EventType - Тип события алерта
type EventType = "THRESHOLD_EXCEEDED" | "CONFIRM" | "RESOLVE" | "START" | "STOP" | "TIMEOUT" | "FALSE_POSITIVE";

//AlertDao - Алерт и его параметры
interface AlertDto {
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
}

//AlertHistoryEntryDao - История алерта и ее параметры
interface AlertHistoryEntryDto {
    id: number;
    fromStatus: AlertStatus;
    toStatus: AlertStatus;
    event: EventType;
    actorUserId: number;
    comment: string;
    createdAt: string;
}

//AlertDetailsDto - Детали алерта и его история
interface AlertDetailsDto extends AlertDto {
    history: AlertHistoryEntryDto[];
}

//Page - Страница данных и ее параметры
interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}

//AlertCreatedEvent - Событие создания алерта и его параметры
interface AlertCreatedEvent {
    alertId: number;
    deviceId: number;
    severity: AlertSeverity;
    eventType: EventType;
    timestamp: string;
    message: string;
}