CREATE TABLE IF NOT EXISTS telemetry_aggregation
(
    sensor_id INT              NOT NULL,
    timestamp TIMESTAMP        NOT NULL,
    count     BIGINT           NOT NULL,
    sum       DOUBLE PRECISION NOT NULL,
    mean      DOUBLE PRECISION NOT NULL,
    max       DOUBLE PRECISION NOT NULL,
    min       DOUBLE PRECISION NOT NULL,
    std_dev    DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (sensor_id, timestamp)
);