package com.pulseboard.core.application.port.in;

import com.pulseboard.core.domain.ActivityEvent;
import java.util.List;

public interface QueryUseCase {
    List<ActivityEvent> recent(int limit);
}
