-- 1. Таблица оборудования (PostgreSQL)
CREATE TABLE devices (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    location_id VARCHAR(100),
    install_date TIMESTAMPTZ
);

-- 2. Таблица датчиков
CREATE TABLE sensors (
    id BIGSERIAL PRIMARY KEY,
    device_id BIGINT NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL,
    unit VARCHAR(50),
    calibration_date TIMESTAMPTZ,
    UNIQUE(device_id, type)
);

-- 3. Гипертаблица телеметрии (TimescaleDB)
CREATE TABLE telemetry (
    time TIMESTAMPTZ NOT NULL,
    sensor_id BIGINT NOT NULL REFERENCES sensors(id) ON DELETE CASCADE,
    value DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (time, sensor_id) 
);

-- Преобразование в гипертаблицу
SELECT create_hypertable(
    'telemetry', 
    'time', 
    chunk_time_interval => INTERVAL '1 day',
    create_default_indexes => FALSE
);

-- 4. Таблица оповещений
CREATE TYPE alert_severity AS ENUM('INFO', 'WARNING', 'CRITICAL');
CREATE TYPE alert_status AS ENUM('NEW', 'ACKNOWLEDGED', 'RESOLVED', 'FALSE_POSITIVE', 'EXPIRED');

CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    device_id BIGINT NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    severity alert_severity NOT NULL,
    message TEXT,
    status alert_status NOT NULL DEFAULT 'NEW'
);

-- Индексы для ускорения запросов
CREATE INDEX idx_telemetry_sensor_time ON telemetry(sensor_id, time DESC);
CREATE INDEX idx_alerts_device_timestamp ON alerts(device_id, timestamp DESC);
CREATE INDEX idx_alerts_status ON alerts(status) WHERE status IN ('NEW', 'ACKNOWLEDGED');
