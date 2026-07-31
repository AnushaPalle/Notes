package com.pulseboard.core.application.port.in;

import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.SourceTypeStat;
import java.util.List;

public interface QueryUseCase {
    List<ActivityEvent> recent(int limit);

    List<SourceTypeStat> stats();
}
