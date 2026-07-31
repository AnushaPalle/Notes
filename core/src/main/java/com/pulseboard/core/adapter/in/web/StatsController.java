package com.pulseboard.core.adapter.in.web;

import com.pulseboard.core.application.port.in.QueryUseCase;
import com.pulseboard.core.application.port.in.ReplayUseCase;
import com.pulseboard.core.domain.SourceTypeStat;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatsController {

    private final QueryUseCase query;
    private final ReplayUseCase replay;

    public StatsController(QueryUseCase query, ReplayUseCase replay) {
        this.query = query;
        this.replay = replay;
    }

    @GetMapping("/stats")
    public List<SourceTypeStat> stats() {
        return query.stats();
    }

    @PostMapping("/admin/replay")
    public Map<String, Integer> replay() {
        return Map.of("replayed", replay.replay());
    }
}
