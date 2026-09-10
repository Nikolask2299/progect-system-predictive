ALTER TABLE telemetry SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'sensor_id',
    timescaledb.compress_orderby = 'time DESC'
);

SELECT add_compression_policy('telemetry', INTERVAL '7 day');
SELECT add_retention_policy('telemetry', INTERVAL '180 days');
