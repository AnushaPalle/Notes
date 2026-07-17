package com.pulseboard.core.application.port.out;

import com.pulseboard.core.domain.ActivityEvent;

/** Fan-out of accepted events to anything that wants to react (logs, live streams, notifiers). */
public interface EventPublisher {
    void publish(ActivityEvent event);
}
