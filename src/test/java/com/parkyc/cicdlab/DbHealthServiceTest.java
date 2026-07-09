package com.parkyc.cicdlab;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DbHealthServiceTest {

    @Test
    void returnsUpWhenSelectOneSucceeds() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);
        DbHealthService service = new DbHealthService(jdbcTemplate);

        DbHealthService.DbHealthResult result = service.check();

        assertThat(result.status()).isEqualTo("UP");
        assertThat(result.database()).isEqualTo("postgres");
        assertThat(result.result()).isEqualTo(1);
        assertThat(result.message()).isEqualTo("Database connection is available");
    }

    @Test
    void returnsDownWithRootCauseWhenSelectOneFails() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .thenThrow(new RuntimeException(
                        "Failed to obtain JDBC Connection",
                        new IllegalStateException("nodename nor servname provided")
                ));
        DbHealthService service = new DbHealthService(jdbcTemplate);

        DbHealthService.DbHealthResult result = service.check();

        assertThat(result.status()).isEqualTo("DOWN");
        assertThat(result.database()).isEqualTo("postgres");
        assertThat(result.result()).isNull();
        assertThat(result.message()).isEqualTo("nodename nor servname provided");
    }
}
