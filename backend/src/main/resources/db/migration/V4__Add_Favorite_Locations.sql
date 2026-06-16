CREATE TABLE IF NOT EXISTS favorite_locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_user_location (user_id, location_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (location_id) REFERENCES parking_locations(id)
);
