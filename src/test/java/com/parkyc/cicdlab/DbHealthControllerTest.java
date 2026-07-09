package com.parkyc.cicdlab;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DbHealthControllerTest {

    @Test
    void returnsUpWhenDatabaseQuerySucceeds() {
        DbHealthService dbHealthService = mock(DbHealthService.class);
        when(dbHealthService.check()).thenReturn(new DbHealthService.DbHealthResult(
                "UP",
                "postgres",
                1,
                "Database connection is available"
        ));
        DbHealthController controller = new DbHealthController(dbHealthService);

        ResponseEntity<DbHealthController.DbHealthResponse> response = controller.health();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo("UP");
        assertThat(response.getBody().database()).isEqualTo("postgres");
        assertThat(response.getBody().result()).isEqualTo(1);
        assertThat(response.getBody().message()).isEqualTo("Database connection is available");
    }

    @Test
    void returnsRootCauseMessageWhenDatabaseQueryFails() {
        DbHealthService dbHealthService = mock(DbHealthService.class);
        when(dbHealthService.check()).thenReturn(new DbHealthService.DbHealthResult(
                "DOWN",
                "postgres",
                null,
                "nodename nor servname provided"
        ));
        DbHealthController controller = new DbHealthController(dbHealthService);

        ResponseEntity<DbHealthController.DbHealthResponse> response = controller.health();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo("DOWN");
        assertThat(response.getBody().message()).isEqualTo("nodename nor servname provided");
    }
}
