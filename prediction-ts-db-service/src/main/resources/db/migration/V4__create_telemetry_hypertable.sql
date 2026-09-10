-- 3. Гипертаблица телеметрии (TimescaleDB)
CREATE TABLE telemetry (
    time TIMESTAMPTZ NOT NULL,
    sensor_id INT NOT NULL REFERENCES sensors(id) ON DELETE SET NULL,
    value DOUBLE PRECISION NOT NULL,
    quality VARCHAR(20) NOT NULL DEFAULT 'GOOD',
    PRIMARY KEY (time, sensor_id)
);

-- Преобразование в гипертаблицу
SELECT create_hypertable(
    'telemetry',
    'time',
    chunk_time_interval => INTERVAL '1 day',
    create_default_indexes => FALSE
);