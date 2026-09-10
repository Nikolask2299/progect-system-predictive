-- Индексы для ускорения запросов
CREATE INDEX idx_telemetry_sensor_time ON telemetry(sensor_id, time DESC);
CREATE INDEX idx_alerts_device_timestamp ON alerts(device_id, create_at DESC);
CREATE INDEX idx_alerts_status ON alerts(status) WHERE status IN ('NEW', 'ACKNOWLEDGED');
CREATE INDEX idx_alerts_severity ON alerts(severity) WHERE severity IN ('CRITICAL', 'WARNING');
CREATE INDEX idx_alerts_severity_status ON alerts(severity, status) WHERE severity IN ('CRITICAL', 'WARNING') AND status IN ('NEW', 'ACKNOWLEDGED');