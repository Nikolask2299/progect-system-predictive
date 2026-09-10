-- 4. Таблица оповещений
CREATE TYPE alert_severity AS ENUM('INFO', 'WARNING', 'CRITICAL');
CREATE TYPE alert_status AS ENUM('NEW', 'ACKNOWLEDGED', 'RESOLVED', 'FALSE_POSITIVE', 'EXPIRED', 'IN_PROGRESS');

CREATE TABLE alerts (
    id SERIAL PRIMARY KEY,
    device_id INT NOT NULL REFERENCES devices(id) ON DELETE RESTRICT,
    sensor_id INT NOT NULL REFERENCES sensors(id) ON DELETE RESTRICT,
    create_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    severity alert_severity NOT NULL,
    message TEXT,
    status alert_status NOT NULL DEFAULT 'NEW'
);