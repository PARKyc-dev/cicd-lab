package com.parkyc.jypword.health.scheduler;

import com.parkyc.jypword.health.service.DbHealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DbHealthScheduler {

    private static final Logger log = LoggerFactory.getLogger(DbHealthScheduler.class);
    private static final long TWELVE_HOURS = 12L * 60L * 60L * 1000L;

    private final DbHealthService dbHealthService;

    public DbHealthScheduler(DbHealthService dbHealthService) {
        this.dbHealthService = dbHealthService;
    }

    @Scheduled(fixedRate = TWELVE_HOURS)
    public void checkDatabase() {
        DbHealthService.DbHealthResult result = dbHealthService.check();
        if (result.isUp()) {
            log.info("Scheduled Supabase health check succeeded: result={}", result.result());
            return;
        }
        log.warn("Scheduled Supabase health check failed: {}", result.message());
    }
}
