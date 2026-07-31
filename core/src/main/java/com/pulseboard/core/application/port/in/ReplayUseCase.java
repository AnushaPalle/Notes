package com.pulseboard.core.application.port.in;

public interface ReplayUseCase {

    /** Drops the read model and rebuilds it from the event log. Returns the number of events replayed. */
    int replay();
}
