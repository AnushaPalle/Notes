package com.pulseboard.core.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseboard.core.adapter.in.web.security.HmacVerifier;
import com.pulseboard.core.adapter.in.web.security.SecurityProperties;
import com.pulseboard.core.application.port.in.SubmitEventUseCase;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.Severity;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Receives events from third parties that cannot hold an API key, authenticated by an HMAC of the raw body.
 * A replayed webhook is harmless: the event id makes ingestion idempotent.
 */
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    record WebhookPayload(String eventId, String type, Severity severity, String payload, Instant occurredAt) {
    }

    private final SubmitEventUseCase submit;
    private final SecurityProperties properties;
    private final ObjectMapper mapper;

    public WebhookController(SubmitEventUseCase submit, SecurityProperties properties, ObjectMapper mapper) {
        this.submit = submit;
        this.properties = properties;
        this.mapper = mapper;
    }

    @PostMapping("/{source}")
    public ResponseEntity<Map<String, String>> receive(@PathVariable String source,
                                                       @RequestHeader(value = "X-Signature", required = false) String signature,
                                                       @RequestBody byte[] body) throws IOException {
        if (!HmacVerifier.verify(properties.webhookSecret(), body, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid signature"));
        }
        var p = mapper.readValue(body, WebhookPayload.class);
        var event = new ActivityEvent(p.eventId(), source, p.type(), p.severity(), p.payload(), p.occurredAt());
        if (!submit.submit(event)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", "queue full"));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("status", "QUEUED"));
    }
}
