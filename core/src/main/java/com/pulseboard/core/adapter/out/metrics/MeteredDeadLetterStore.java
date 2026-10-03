package com.pulseboard.core.adapter.out.metrics;

import com.pulseboard.core.adapter.out.jdbc.JdbcDeadLetterStore;
import com.pulseboard.core.application.port.out.DeadLetterStore;
import com.pulseboard.core.domain.DeadLetter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** Decorator: every dead letter bumps a counter that the DLQ alert watches. */
@Primary
@Component
public class MeteredDeadLetterStore implements DeadLetterStore {

    private final JdbcDeadLetterStore delegate;
    private final MeterRegistry registry;

    public MeteredDeadLetterStore(JdbcDeadLetterStore delegate, MeterRegistry registry) {
        this.delegate = delegate;
        this.registry = registry;
    }

    @Override
    public void save(DeadLetter deadLetter) {
        delegate.save(deadLetter);
        registry.counter("pulseboard.events.dead_lettered").increment();
    }

    @Override
    public List<DeadLetter> recent(int limit) {
        return delegate.recent(limit);
    }
}
