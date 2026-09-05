package com.pulseboard.core.adapter.in.stream;

import com.pulseboard.core.application.port.out.EventPublisher;
import com.pulseboard.core.domain.ActivityEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/** Keeps the open browser connections and pushes every accepted event to them (Server-Sent Events). */
@Component
public class SseHub implements EventPublisher {

    private static final long TIMEOUT_MS = 30 * 60 * 1000L;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        emitters.add(emitter);
        Runnable remove = () -> emitters.remove(emitter);
        emitter.onCompletion(remove);
        emitter.onTimeout(remove);
        emitter.onError(t -> remove.run());
        return emitter;
    }

    @Override
    public void publish(ActivityEvent event) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("activity").id(event.eventId())
                        .data(event, MediaType.APPLICATION_JSON));
            } catch (IOException | IllegalStateException gone) {
                emitters.remove(emitter);
            }
        }
    }

    int subscriberCount() {
        return emitters.size();
    }
}
