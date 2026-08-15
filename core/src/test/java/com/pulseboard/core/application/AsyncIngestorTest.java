package com.pulseboard.core.application;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pulseboard.core.application.port.in.IngestUseCase;
import com.pulseboard.core.application.port.in.IngestUseCase.Result;
import com.pulseboard.core.application.port.out.DeadLetterStore;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.DeadLetter;
import com.pulseboard.core.domain.Severity;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class AsyncIngestorTest {

    private final List<DeadLetter> dead = new CopyOnWriteArrayList<>();
    private final DeadLetterStore deadLetters = new DeadLetterStore() {
        public void save(DeadLetter d) {
            dead.add(d);
        }

        public List<DeadLetter> recent(int limit) {
            return dead;
        }
    };

    private static ActivityEvent event(String id) {
        return new ActivityEvent(id, "svc", "t", Severity.INFO, "", null);
    }

    @Test
    void retriesTransientFailuresThenSucceeds() {
        var calls = new AtomicInteger();
        IngestUseCase flaky = e -> {
            if (calls.incrementAndGet() < 3) {
                throw new IllegalStateException("db down");
            }
            return Result.ACCEPTED;
        };
        var ingestor = new AsyncIngestor(flaky, deadLetters, 3, 1, 10);

        assertTrue(ingestor.submit(event("e1")));

        await().atMost(5, TimeUnit.SECONDS).until(() -> calls.get() == 3);
        ingestor.shutdown();
        assertTrue(dead.isEmpty());
    }

    @Test
    void movesEventToDeadLettersAfterMaxAttempts() {
        var calls = new AtomicInteger();
        IngestUseCase broken = e -> {
            calls.incrementAndGet();
            throw new IllegalStateException("permanent failure");
        };
        var ingestor = new AsyncIngestor(broken, deadLetters, 3, 1, 10);

        ingestor.submit(event("e1"));

        await().atMost(5, TimeUnit.SECONDS).until(() -> dead.size() == 1);
        ingestor.shutdown();
        assertEquals(3, calls.get());
        assertEquals("e1", dead.get(0).event().eventId());
        assertEquals("permanent failure", dead.get(0).error());
    }

    @Test
    void rejectsWhenQueueIsFull() throws Exception {
        var started = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        IngestUseCase blocking = e -> {
            started.countDown();
            try {
                release.await();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return Result.ACCEPTED;
        };
        var ingestor = new AsyncIngestor(blocking, deadLetters, 1, 1, 1);

        assertTrue(ingestor.submit(event("running")));
        assertTrue(started.await(5, TimeUnit.SECONDS));
        assertTrue(ingestor.submit(event("queued")));
        assertFalse(ingestor.submit(event("rejected")));

        release.countDown();
        ingestor.shutdown();
    }
}
