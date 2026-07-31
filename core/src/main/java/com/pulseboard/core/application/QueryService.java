package com.pulseboard.core.application;

import com.pulseboard.core.application.port.in.QueryUseCase;
import com.pulseboard.core.application.port.out.EventStore;
import com.pulseboard.core.application.port.out.ReadModel;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.SourceTypeStat;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QueryService implements QueryUseCase {

    private static final int MAX_LIMIT = 500;

    private final EventStore store;
    private final ReadModel readModel;

    public QueryService(EventStore store, ReadModel readModel) {
        this.store = store;
        this.readModel = readModel;
    }

    @Override
    public List<ActivityEvent> recent(int limit) {
        return store.recent(Math.max(1, Math.min(limit, MAX_LIMIT)));
    }

    @Override
    public List<SourceTypeStat> stats() {
        return readModel.stats();
    }
}
