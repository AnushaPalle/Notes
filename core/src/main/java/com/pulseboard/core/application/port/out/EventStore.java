package com.pulseboard.core.application.port.out;

import com.pulseboard.core.domain.ActivityEvent;
import java.util.List;

public interface EventStore {

    /** Appends the event. Returns false when an event with the same id already exists. */
    boolean append(ActivityEvent event);

    /** Newest first. */
    List<ActivityEvent> recent(int limit);

    /** Every event in append order; used to rebuild projections. */
    List<ActivityEvent> all();
}
