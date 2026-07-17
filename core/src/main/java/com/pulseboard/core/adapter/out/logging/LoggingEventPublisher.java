package com.pulseboard.core.adapter.out.logging;

import com.pulseboard.core.application.port.out.EventPublisher;
import com.pulseboard.core.domain.ActivityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisher.class);

    @Override
    public void publish(ActivityEvent event) {
        log.info("event accepted id={} source={} type={} severity={}",
                event.eventId(), event.source(), event.type(), event.severity());
    }
}
