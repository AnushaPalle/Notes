package com.pulseboard.core.adapter.in.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.Severity;
import org.junit.jupiter.api.Test;

class SseHubTest {

    private static final ActivityEvent EVENT = new ActivityEvent("e1", "svc", "t", Severity.INFO, "", null);

    @Test
    void subscribersReceivePublishedEventsWithoutError() {
        var hub = new SseHub();
        hub.subscribe();

        hub.publish(EVENT);

        assertEquals(1, hub.subscriberCount());
    }

    @Test
    void deadSubscribersAreDroppedOnNextPublish() {
        var hub = new SseHub();
        hub.subscribe().complete();
        hub.subscribe();

        hub.publish(EVENT);

        assertEquals(1, hub.subscriberCount());
    }
}
