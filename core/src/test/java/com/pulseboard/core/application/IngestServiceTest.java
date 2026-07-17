package com.pulseboard.core.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pulseboard.core.adapter.out.memory.InMemoryEventStore;
import com.pulseboard.core.application.port.in.IngestUseCase.Result;
import com.pulseboard.core.application.port.out.EventPublisher;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.Severity;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class IngestServiceTest {

    private final List<ActivityEvent> published = new ArrayList<>();
    private final EventPublisher recording = published::add;

    private static ActivityEvent event(String id) {
        return new ActivityEvent(id, "billing", "invoice.failed", Severity.ERROR, "{}", null);
    }

    @Test
    void acceptsNewEventAndPublishesIt() {
        var service = new IngestService(new InMemoryEventStore(), List.of(recording));

        assertEquals(Result.ACCEPTED, service.ingest(event("e1")));
        assertEquals(1, published.size());
    }

    @Test
    void duplicateEventIsNotPublishedAgain() {
        var service = new IngestService(new InMemoryEventStore(), List.of(recording));
        service.ingest(event("e1"));

        assertEquals(Result.DUPLICATE, service.ingest(event("e1")));
        assertEquals(1, published.size());
    }

    @Test
    void failingPublisherDoesNotFailIngestion() {
        EventPublisher broken = e -> { throw new IllegalStateException("boom"); };
        var service = new IngestService(new InMemoryEventStore(), List.of(broken, recording));

        assertEquals(Result.ACCEPTED, service.ingest(event("e1")));
        assertEquals(1, published.size());
    }

    @Test
    void blankIdIsRejectedByTheDomain() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ActivityEvent(" ", "s", "t", null, null, null));
    }
}
