package com.tujulishanehub.backend;

import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TujulishaneHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(TujulishaneHubApplication.class, args);
    }

    @Bean
    public FlywayMigrationStrategy repairStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}