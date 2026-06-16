package com.smartparking.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            // Programmatically repair the schema history table before migrating.
            // This fixes checksum mismatches and failed migration states automatically.
            flyway.repair();
            flyway.migrate();
        };
    }
}
