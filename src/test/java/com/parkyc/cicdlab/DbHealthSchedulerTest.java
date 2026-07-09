package com.parkyc.cicdlab;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DbHealthSchedulerTest {

    @Test
    void runsDatabaseHealthCheck() {
        DbHealthService dbHealthService = mock(DbHealthService.class);
        when(dbHealthService.check()).thenReturn(new DbHealthService.DbHealthResult(
                "UP",
                "postgres",
                1,
                "Database connection is available"
        ));
        DbHealthScheduler scheduler = new DbHealthScheduler(dbHealthService);

        scheduler.checkDatabase();

        verify(dbHealthService).check();
    }
}
