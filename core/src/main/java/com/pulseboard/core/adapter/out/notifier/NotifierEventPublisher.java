package com.pulseboard.core.adapter.out.notifier;

import com.pulseboard.core.application.port.out.EventPublisher;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.Severity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/** Forwards WARN and ERROR events to the notifier function; INFO noise never leaves the core. */
public class NotifierEventPublisher implements EventPublisher {

    private final RestClient client;

    public NotifierEventPublisher(RestClient client) {
        this.client = client;
    }

    @Override
    public void publish(ActivityEvent event) {
        if (!event.severity().atLeast(Severity.WARN)) {
            return;
        }
        client.post().uri("/notify").contentType(MediaType.APPLICATION_JSON).body(event)
                .retrieve().toBodilessEntity();
    }
}
