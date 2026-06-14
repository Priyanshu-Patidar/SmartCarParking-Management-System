-- SmartPark Initial Schema

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    blocked BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expiry DATETIME(6),
    failed_login_attempts INT DEFAULT 0,
    lockout_until DATETIME(6),
    last_login_at DATETIME(6),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE parking_locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    zip_code VARCHAR(20),
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    image_url VARCHAR(500),
    hourly_rate DECIMAL(10, 2) NOT NULL,
    peak_hour_rate DECIMAL(10, 2),
    bike_rate DECIMAL(10, 2),
    ev_rate DECIMAL(10, 2),
    ev_charging_available BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    open_time TIME,
    close_time TIME,
    description TEXT,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE parking_vehicle_types (
    location_id BIGINT NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (location_id) REFERENCES parking_locations(id)
);

CREATE TABLE parking_floors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    floor_number INT NOT NULL,
    floor_name VARCHAR(100),
    location_id BIGINT NOT NULL,
    FOREIGN KEY (location_id) REFERENCES parking_locations(id)
);

CREATE TABLE parking_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_number VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    ev_charging BOOLEAN DEFAULT FALSE,
    floor_id BIGINT NOT NULL,
    FOREIGN KEY (floor_id) REFERENCES parking_floors(id)
);

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    start_time DATETIME(6) NOT NULL,
    end_time DATETIME(6) NOT NULL,
    duration_hours INT NOT NULL,
    estimated_fee DECIMAL(10, 2) NOT NULL,
    actual_fee DECIMAL(10, 2),
    status VARCHAR(50) NOT NULL,
    qr_code_data TEXT,
    vehicle_number VARCHAR(20),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (location_id) REFERENCES parking_locations(id),
    FOREIGN KEY (slot_id) REFERENCES parking_slots(id)
);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(50),
    payment_method VARCHAR(30),
    paid_at DATETIME(6),
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE blacklisted_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL
);

CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(120),
    action VARCHAR(100) NOT NULL,
    details VARCHAR(500),
    ip_address VARCHAR(50),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE search_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    query VARCHAR(300) NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    searched_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (location_id) REFERENCES parking_locations(id)
);

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE parking_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    check_in_time DATETIME(6),
    check_out_time DATETIME(6),
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    final_amount DECIMAL(10, 2),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE INDEX idx_location_city ON parking_locations(city);
CREATE INDEX idx_location_coords ON parking_locations(latitude, longitude);
CREATE INDEX idx_booking_slot_time ON bookings(slot_id, start_time, end_time);
