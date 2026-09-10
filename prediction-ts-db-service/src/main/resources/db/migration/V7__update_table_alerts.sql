
BEGIN;

ALTER TABLE alerts DROP CONSTRAINT alerts_sensor_id_fkey;

ALTER TABLE alerts
    ADD CONSTRAINT alerts_sensor_id_fkey FOREIGN KEY (sensor_id) REFERENCES prediction_schema.sensors(id) ON DELETE SET NULL;

ALTER TABLE alerts RENAME COLUMN create_at TO created_at;

COMMIT;