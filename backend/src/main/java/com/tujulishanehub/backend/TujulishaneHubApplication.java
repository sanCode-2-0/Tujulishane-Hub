package com.tujulishanehub.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TujulishaneHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(TujulishaneHubApplication.class, args);
    }
}