package com.pulseboard.core.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pulseboard.core.application.port.in.IngestUseCase.Result;
import com.pulseboard.core.application.port.out.EventPublisher;
import com.pulseboard.core.application.port.out.EventStore;
import com.pulseboard.core.application.port.out.ReadModel;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.Severity;
import com.pulseboard.core.domain.SourceTypeStat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class IngestServiceTest {

    static class FakeStore implements EventStore {
        final Map<String, ActivityEvent> byId = new LinkedHashMap<>();

        public boolean append(ActivityEvent e) {
            return byId.putIfAbsent(e.eventId(), e) == null;
        }

        public List<ActivityEvent> recent(int limit) {
            var all = new ArrayList<>(byId.values());
            java.util.Collections.reverse(all);
            return all.subList(0, Math.min(limit, all.size()));
        }

        public List<ActivityEvent> all() {
            return new ArrayList<>(byId.values());
        }
    }

    static class FakeReadModel implements ReadModel {
        final Map<String, Long> counts = new LinkedHashMap<>();

        public void apply(ActivityEvent e) {
            counts.merge(e.source() + "/" + e.type(), 1L, Long::sum);
        }

        public void clear() {
            counts.clear();
        }

        public List<SourceTypeStat> stats() {
            return List.of();
        }
    }

    private final List<ActivityEvent> published = new ArrayList<>();
    private final EventPublisher recording = published::add;
    private final FakeStore store = new FakeStore();
    private final FakeReadModel readModel = new FakeReadModel();

    private static ActivityEvent event(String id) {
        return new ActivityEvent(id, "billing", "invoice.failed", Severity.ERROR, "{}", null);
    }

    @Test
    void acceptsNewEventProjectsAndPublishesIt() {
        var service = new IngestService(store, readModel, List.of(recording));

        assertEquals(Result.ACCEPTED, service.ingest(event("e1")));
        assertEquals(1, published.size());
        assertEquals(1L, readModel.counts.get("billing/invoice.failed"));
    }

    @Test
    void duplicateEventIsNeitherProjectedNorPublishedAgain() {
        var service = new IngestService(store, readModel, List.of(recording));
        service.ingest(event("e1"));

        assertEquals(Result.DUPLICATE, service.ingest(event("e1")));
        assertEquals(1, published.size());
        assertEquals(1L, readModel.counts.get("billing/invoice.failed"));
    }

    @Test
    void failingPublisherDoesNotFailIngestion() {
        EventPublisher broken = e -> { throw new IllegalStateException("boom"); };
        var service = new IngestService(store, readModel, List.of(broken, recording));

        assertEquals(Result.ACCEPTED, service.ingest(event("e1")));
        assertEquals(1, published.size());
    }

    @Test
    void replayRebuildsTheReadModelFromTheLog() {
        var ingest = new IngestService(store, readModel, List.of());
        ingest.ingest(event("e1"));
        ingest.ingest(event("e2"));
        readModel.counts.put("billing/invoice.failed", 99L);

        int replayed = new ReplayService(store, readModel).replay();

        assertEquals(2, replayed);
        assertEquals(2L, readModel.counts.get("billing/invoice.failed"));
    }

    @Test
    void blankIdIsRejectedByTheDomain() {
        assertThrows(IllegalArgumentException.class, () -> new ActivityEvent(" ", "s", "t", null, null, null));
    }
}
