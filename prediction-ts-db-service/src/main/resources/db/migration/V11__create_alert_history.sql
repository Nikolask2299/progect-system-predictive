CREATE TYPE alert_event AS ENUM ('CONFIRM', 'START_WORK', 'RESOLVE', 'MARK_FALSE', 'TIMEOUT');

CREATE TABLE alert_history (
    id BIGSERIAL PRIMARY KEY,
    alert_id BIGINT NOT NULL REFERENCES alerts(id) ON DELETE CASCADE,
    from_status alert_status,
    to_status alert_status NOT NULL,
    actor_user_id BIGINT REFERENCES users(id),
    event alert_event NOT NULL DEFAULT 'CONFIRM',
    comment TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_alert_history_alert_time ON alert_history(alert_id, created_at DESC);

CREATE UNIQUE INDEX idx_alerts_active_unique ON alerts(device_id, event_type) WHERE status IN ('NEW', 'ACKNOWLEDGED');