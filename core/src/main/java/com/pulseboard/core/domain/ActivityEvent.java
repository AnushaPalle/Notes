package com.pulseboard.core.domain;

import java.time.Instant;

/** An immutable fact: something happened in a source system. */
public record ActivityEvent(String eventId, String source, String type, Severity severity,
                            String payload, Instant occurredAt) {

    public ActivityEvent {
        eventId = requireText(eventId, "eventId");
        source = requireText(source, "source");
        type = requireText(type, "type");
        severity = severity == null ? Severity.INFO : severity;
        payload = payload == null ? "" : payload;
        occurredAt = occurredAt == null ? Instant.now() : occurredAt;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
