package com.parkyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UnifiedApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnifiedApplication.class, args);
    }
}
