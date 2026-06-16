-- Envers Audit Tables

CREATE TABLE IF NOT EXISTS REVINFO (
    REV INT AUTO_INCREMENT PRIMARY KEY,
    REVTSTMP BIGINT
);

CREATE TABLE IF NOT EXISTS users_audit (
    id BIGINT NOT NULL,
    REV INT NOT NULL,
    REVTYPE TINYINT,
    email VARCHAR(100),
    full_name VARCHAR(100),
    phone VARCHAR(20),
    enabled BOOLEAN,
    blocked BOOLEAN,
    email_verified BOOLEAN,
    deleted BOOLEAN,
    PRIMARY KEY (id, REV),
    FOREIGN KEY (REV) REFERENCES REVINFO(REV)
);

CREATE TABLE IF NOT EXISTS parking_locations_audit (
    id BIGINT NOT NULL,
    REV INT NOT NULL,
    REVTYPE TINYINT,
    name VARCHAR(150),
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    latitude DOUBLE,
    longitude DOUBLE,
    hourly_rate DECIMAL(10, 2),
    active BOOLEAN,
    deleted BOOLEAN,
    PRIMARY KEY (id, REV),
    FOREIGN KEY (REV) REFERENCES REVINFO(REV)
);

CREATE TABLE IF NOT EXISTS bookings_audit (
    id BIGINT NOT NULL,
    REV INT NOT NULL,
    REVTYPE TINYINT,
    booking_code VARCHAR(50),
    user_id BIGINT,
    location_id BIGINT,
    slot_id BIGINT,
    vehicle_type VARCHAR(50),
    start_time DATETIME(6),
    end_time DATETIME(6),
    status VARCHAR(50),
    actual_fee DECIMAL(10, 2),
    deleted BOOLEAN,
    PRIMARY KEY (id, REV),
    FOREIGN KEY (REV) REFERENCES REVINFO(REV)
);
