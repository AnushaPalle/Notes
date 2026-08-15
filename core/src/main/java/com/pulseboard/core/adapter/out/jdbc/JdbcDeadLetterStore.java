package com.pulseboard.core.adapter.out.jdbc;

import com.pulseboard.core.application.port.out.DeadLetterStore;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.DeadLetter;
import com.pulseboard.core.domain.Severity;
import java.time.Instant;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcDeadLetterStore implements DeadLetterStore {

    private final JdbcTemplate jdbc;

    public JdbcDeadLetterStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(DeadLetter d) {
        var e = d.event();
        String error = d.error() == null ? "" : d.error().substring(0, Math.min(d.error().length(), 1000));
        jdbc.update("""
                INSERT INTO dead_letters (event_id, source, type, severity, payload, occurred_at, error, attempts, failed_at)
                VALUES (?,?,?,?,?,?,?,?,?)""",
                e.eventId(), e.source(), e.type(), e.severity().name(), e.payload(), e.occurredAt().toString(),
                error, d.attempts(), d.failedAt().toString());
    }

    @Override
    public List<DeadLetter> recent(int limit) {
        return jdbc.query("SELECT * FROM dead_letters ORDER BY id DESC LIMIT ?", (rs, i) -> new DeadLetter(
                new ActivityEvent(rs.getString("event_id"), rs.getString("source"), rs.getString("type"),
                        Severity.valueOf(rs.getString("severity")), rs.getString("payload"),
                        Instant.parse(rs.getString("occurred_at"))),
                rs.getString("error"), rs.getInt("attempts"), Instant.parse(rs.getString("failed_at"))), limit);
    }
}
