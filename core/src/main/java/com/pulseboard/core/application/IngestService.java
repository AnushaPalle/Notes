package com.pulseboard.core.application;

import com.pulseboard.core.application.port.in.IngestUseCase;
import com.pulseboard.core.application.port.out.EventPublisher;
import com.pulseboard.core.application.port.out.EventStore;
import com.pulseboard.core.domain.ActivityEvent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IngestService implements IngestUseCase {

    private static final Logger log = LoggerFactory.getLogger(IngestService.class);

    private final EventStore store;
    private final List<EventPublisher> publishers;

    public IngestService(EventStore store, List<EventPublisher> publishers) {
        this.store = store;
        this.publishers = publishers;
    }

    @Override
    public Result ingest(ActivityEvent event) {
        if (!store.append(event)) {
            return Result.DUPLICATE;
        }
        for (EventPublisher publisher : publishers) {
            try {
                publisher.publish(event);
            } catch (RuntimeException e) {
                // a broken subscriber must never fail ingestion
                log.warn("publisher {} failed for event {}", publisher.getClass().getSimpleName(), event.eventId(), e);
            }
        }
        return Result.ACCEPTED;
    }
}
