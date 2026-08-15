package com.pulseboard.core.application.port.in;

import com.pulseboard.core.domain.ActivityEvent;

public interface SubmitEventUseCase {

    /** Queues the event for processing. Returns false when the queue is full (back-pressure). */
    boolean submit(ActivityEvent event);
}
