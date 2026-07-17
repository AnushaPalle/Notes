package com.pulseboard.core.application.port.in;

import com.pulseboard.core.domain.ActivityEvent;

public interface IngestUseCase {

    enum Result { ACCEPTED, DUPLICATE }

    Result ingest(ActivityEvent event);
}
