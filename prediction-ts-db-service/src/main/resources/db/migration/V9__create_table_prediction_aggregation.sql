CREATE TYPE prediction_source AS ENUM('ACTUAL', 'CACHED', 'UNSPECIFIED');

CREATE TABLE IF NOT EXISTS prediction_aggregation(
    id SERIAL PRIMARY KEY,
    device_id INT NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    rul_hours DOUBLE PRECISION NOT NULL,
    confidence DOUBLE PRECISION NOT NULL,
    risk_score DOUBLE PRECISION NOT NULL,
    source prediction_source NOT NULL,
    calculate_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    maintenance_recommendation TEXT
);

CREATE INDEX idx_prediction_device_create ON prediction_aggregation(device_id, calculate_at);
CREATE INDEX idx_prediction_device_source ON prediction_aggregation(device_id, source);
