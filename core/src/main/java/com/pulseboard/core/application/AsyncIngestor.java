package com.pulseboard.core.application;

import com.pulseboard.core.application.port.in.IngestUseCase;
import com.pulseboard.core.application.port.in.SubmitEventUseCase;
import com.pulseboard.core.application.port.out.DeadLetterStore;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.DeadLetter;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Producer/consumer buffer in front of ingestion: the API acknowledges quickly, a single worker processes in order,
 * transient failures are retried with backoff, and exhausted events go to the dead-letter store.
 * Idempotency is provided downstream by the unique event id.
 */
@Component
public class AsyncIngestor implements SubmitEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(AsyncIngestor.class);

    private final IngestUseCase ingest;
    private final DeadLetterStore deadLetters;
    private final int maxAttempts;
    private final long backoffMs;
    private final ThreadPoolExecutor executor;

    public AsyncIngestor(IngestUseCase ingest, DeadLetterStore deadLetters,
                         @Value("${pulseboard.ingest.max-attempts:3}") int maxAttempts,
                         @Value("${pulseboard.ingest.backoff-ms:200}") long backoffMs,
                         @Value("${pulseboard.ingest.queue-capacity:1000}") int queueCapacity) {
        this.ingest = ingest;
        this.deadLetters = deadLetters;
        this.maxAttempts = maxAttempts;
        this.backoffMs = backoffMs;
        this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity), runnable -> {
                    Thread t = new Thread(runnable, "ingest-worker");
                    t.setDaemon(true);
                    return t;
                });
    }

    @Override
    public boolean submit(ActivityEvent event) {
        try {
            executor.execute(() -> process(event));
            return true;
        } catch (RejectedExecutionException full) {
            return false;
        }
    }

    private void process(ActivityEvent event) {
        RuntimeException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                var result = ingest.ingest(event);
                if (result == IngestUseCase.Result.DUPLICATE) {
                    log.debug("duplicate event {} ignored", event.eventId());
                }
                return;
            } catch (RuntimeException e) {
                last = e;
                log.warn("ingest attempt {}/{} failed for {}", attempt, maxAttempts, event.eventId(), e);
                if (attempt < maxAttempts && !sleep(backoffMs * attempt)) {
                    break;
                }
            }
        }
        deadLetters.save(new DeadLetter(event, String.valueOf(last == null ? "interrupted" : last.getMessage()),
                maxAttempts, Instant.now()));
    }

    private boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
