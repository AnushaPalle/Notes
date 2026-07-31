package com.pulseboard.core.application.port.out;

import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.SourceTypeStat;
import java.util.List;

/** The query side of CQRS: a denormalised view derived from the event store. */
public interface ReadModel {
    void apply(ActivityEvent event);

    void clear();

    List<SourceTypeStat> stats();
}
