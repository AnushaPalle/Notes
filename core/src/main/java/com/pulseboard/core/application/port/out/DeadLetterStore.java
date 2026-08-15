package com.pulseboard.core.application.port.out;

import com.pulseboard.core.domain.DeadLetter;
import java.util.List;

public interface DeadLetterStore {
    void save(DeadLetter deadLetter);

    /** Newest first. */
    List<DeadLetter> recent(int limit);
}
