package com.pulseboard.core.adapter.out.jdbc;

import com.pulseboard.core.application.port.out.EventStore;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.Severity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEventStore implements EventStore {

    private static final RowMapper<ActivityEvent> MAPPER = JdbcEventStore::map;

    private final JdbcTemplate jdbc;

    public JdbcEventStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean append(ActivityEvent e) {
        try {
            jdbc.update("INSERT INTO events (event_id, source, type, severity, payload, occurred_at) VALUES (?,?,?,?,?,?)",
                    e.eventId(), e.source(), e.type(), e.severity().name(), e.payload(), e.occurredAt().toString());
            return true;
        } catch (DuplicateKeyException duplicate) {
            return false;
        }
    }

    @Override
    public List<ActivityEvent> recent(int limit) {
        return jdbc.query("SELECT * FROM events ORDER BY seq DESC LIMIT ?", MAPPER, limit);
    }

    @Override
    public List<ActivityEvent> all() {
        return jdbc.query("SELECT * FROM events ORDER BY seq", MAPPER);
    }

    private static ActivityEvent map(ResultSet rs, int row) throws SQLException {
        return new ActivityEvent(rs.getString("event_id"), rs.getString("source"), rs.getString("type"),
                Severity.valueOf(rs.getString("severity")), rs.getString("payload"),
                Instant.parse(rs.getString("occurred_at")));
    }
}
