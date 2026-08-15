package com.pulseboard.core.application;

import com.pulseboard.core.application.port.in.DeadLetterUseCase;
import com.pulseboard.core.application.port.out.DeadLetterStore;
import com.pulseboard.core.domain.DeadLetter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterService implements DeadLetterUseCase {

    private final DeadLetterStore store;

    public DeadLetterService(DeadLetterStore store) {
        this.store = store;
    }

    @Override
    public List<DeadLetter> list(int limit) {
        return store.recent(Math.max(1, Math.min(limit, 500)));
    }
}
