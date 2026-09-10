-- 1. Таблица оборудования (PostgreSQL)
CREATE TABLE devices (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    location_id VARCHAR(100),
    install_date TIMESTAMPTZ
);

-- 2. Таблица датчиков
CREATE TABLE sensors (
    id SERIAL PRIMARY KEY,
    device_id INT NOT NULL REFERENCES devices(id) ON DELETE RESTRICT,
    type VARCHAR(100) NOT NULL,
    unit VARCHAR(50),
    calibration_date TIMESTAMPTZ,
    UNIQUE(device_id, type)
);
