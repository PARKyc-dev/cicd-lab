package com.parkyc.jypword.health.controller;

import com.parkyc.jypword.health.service.DbHealthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/word/db")
public class DbHealthController {

    private final DbHealthService dbHealthService;

    public DbHealthController(DbHealthService dbHealthService) {
        this.dbHealthService = dbHealthService;
    }

    @GetMapping("/health")
    public ResponseEntity<DbHealthResponse> health() {
        DbHealthService.DbHealthResult result = dbHealthService.check();
        if (result.isUp()) {
            return ResponseEntity.ok(new DbHealthResponse(
                    result.status(),
                    result.database(),
                    result.result(),
                    result.message()
            ));
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new DbHealthResponse(
                        result.status(),
                        result.database(),
                        result.result(),
                        result.message()
                ));
    }

    public record DbHealthResponse(
            String status,
            String database,
            Integer result,
            String message
    ) {
    }
}
