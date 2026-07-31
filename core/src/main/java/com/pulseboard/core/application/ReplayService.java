package com.pulseboard.core.application;

import com.pulseboard.core.application.port.in.ReplayUseCase;
import com.pulseboard.core.application.port.out.EventStore;
import com.pulseboard.core.application.port.out.ReadModel;
import com.pulseboard.core.domain.ActivityEvent;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReplayService implements ReplayUseCase {

    private final EventStore store;
    private final ReadModel readModel;

    public ReplayService(EventStore store, ReadModel readModel) {
        this.store = store;
        this.readModel = readModel;
    }

    @Override
    @Transactional
    public int replay() {
        List<ActivityEvent> all = store.all();
        readModel.clear();
        all.forEach(readModel::apply);
        return all.size();
    }
}
