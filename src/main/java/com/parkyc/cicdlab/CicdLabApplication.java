package com.parkyc.cicdlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CicdLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(CicdLabApplication.class, args);
    }

}
