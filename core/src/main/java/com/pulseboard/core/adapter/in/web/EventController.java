package com.pulseboard.core.adapter.in.web;

import com.pulseboard.core.application.port.in.QueryUseCase;
import com.pulseboard.core.application.port.in.SubmitEventUseCase;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.Severity;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

    public record EventRequest(String eventId, String source, String type, Severity severity,
                               String payload, Instant occurredAt) {
        ActivityEvent toDomain() {
            return new ActivityEvent(eventId, source, type, severity, payload, occurredAt);
        }
    }

    private final SubmitEventUseCase submit;
    private final QueryUseCase query;

    public EventController(SubmitEventUseCase submit, QueryUseCase query) {
        this.submit = submit;
        this.query = query;
    }

    /** 202: queued, processed asynchronously. Duplicates are accepted and silently dropped (idempotent). */
    @PostMapping
    public ResponseEntity<Map<String, String>> post(@RequestBody EventRequest request) {
        if (submit.submit(request.toDomain())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("status", "QUEUED"));
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, "1")
                .body(Map.of("error", "ingest queue is full, retry later"));
    }

    @GetMapping
    public List<ActivityEvent> recent(@RequestParam(defaultValue = "50") int limit) {
        return query.recent(limit);
    }
}
