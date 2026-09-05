package com.parkyc.jypword.health.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DbHealthService {

    private final JdbcTemplate jdbcTemplate;

    public DbHealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DbHealthResult check() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return new DbHealthResult("UP", "postgres", result, "Database connection is available");
        } catch (RuntimeException exception) {
            return new DbHealthResult("DOWN", "postgres", null, rootCauseMessage(exception));
        }
    }

    private String rootCauseMessage(RuntimeException exception) {
        Throwable cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage();
    }

    public record DbHealthResult(
            String status,
            String database,
            Integer result,
            String message
    ) {
        public boolean isUp() {
            return "UP".equals(status);
        }
    }
}
