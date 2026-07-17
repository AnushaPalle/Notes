package com.pulseboard.core.adapter.in.web;

import com.pulseboard.core.application.port.in.IngestUseCase;
import com.pulseboard.core.application.port.in.QueryUseCase;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.Severity;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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

    private final IngestUseCase ingest;
    private final QueryUseCase query;

    public EventController(IngestUseCase ingest, QueryUseCase query) {
        this.ingest = ingest;
        this.query = query;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> post(@RequestBody EventRequest request) {
        var result = ingest.ingest(request.toDomain());
        var status = result == IngestUseCase.Result.ACCEPTED ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(Map.of("status", result.name()));
    }

    @GetMapping
    public List<ActivityEvent> recent(@RequestParam(defaultValue = "50") int limit) {
        return query.recent(limit);
    }
}
