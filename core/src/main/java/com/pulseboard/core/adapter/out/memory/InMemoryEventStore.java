package com.pulseboard.core.adapter.out.memory;

import com.pulseboard.core.application.port.out.EventStore;
import com.pulseboard.core.domain.ActivityEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class InMemoryEventStore implements EventStore {

    private final List<ActivityEvent> events = new ArrayList<>();
    private final Set<String> ids = new HashSet<>();

    @Override
    public synchronized boolean append(ActivityEvent event) {
        if (!ids.add(event.eventId())) {
            return false;
        }
        events.add(event);
        return true;
    }

    @Override
    public synchronized List<ActivityEvent> recent(int limit) {
        int from = Math.max(0, events.size() - limit);
        List<ActivityEvent> slice = new ArrayList<>(events.subList(from, events.size()));
        java.util.Collections.reverse(slice);
        return slice;
    }
}
