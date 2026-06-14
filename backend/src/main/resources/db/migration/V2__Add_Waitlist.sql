CREATE TABLE waitlist_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    preferred_start_time DATETIME(6),
    duration_hours INT,
    notified BOOLEAN DEFAULT FALSE,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (location_id) REFERENCES parking_locations(id)
);
