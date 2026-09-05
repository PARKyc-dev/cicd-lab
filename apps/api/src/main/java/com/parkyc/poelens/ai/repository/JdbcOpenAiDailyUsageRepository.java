package com.parkyc.poelens.ai.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class JdbcOpenAiDailyUsageRepository implements OpenAiDailyUsageRepository {
    private static final String INCREMENT_USAGE = """
            INSERT INTO openai_daily_usage (usage_date, request_count)
            VALUES (?, 1)
            ON CONFLICT (usage_date) DO UPDATE
            SET request_count = openai_daily_usage.request_count + 1
            WHERE openai_daily_usage.request_count < ?
            RETURNING request_count
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcOpenAiDailyUsageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean tryIncrement(LocalDate date, int limit) {
        return !jdbcTemplate.query(INCREMENT_USAGE, (resultSet, rowNumber) -> resultSet.getInt(1), date, limit).isEmpty();
    }
}
