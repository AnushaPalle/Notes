package com.pulseboard.core.adapter.out.metrics;

import com.pulseboard.core.application.IngestService;
import com.pulseboard.core.application.port.in.IngestUseCase;
import com.pulseboard.core.domain.ActivityEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** Decorator: counts ingest outcomes without touching the application core. */
@Primary
@Component
public class MeteredIngestUseCase implements IngestUseCase {

    private final IngestService delegate;
    private final MeterRegistry registry;

    public MeteredIngestUseCase(IngestService delegate, MeterRegistry registry) {
        this.delegate = delegate;
        this.registry = registry;
    }

    @Override
    public Result ingest(ActivityEvent event) {
        try {
            Result result = delegate.ingest(event);
            count(result.name().toLowerCase());
            return result;
        } catch (RuntimeException e) {
            count("error");
            throw e;
        }
    }

    private void count(String result) {
        registry.counter("pulseboard.events.ingested", "result", result).increment();
    }
}
