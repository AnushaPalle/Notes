package com.pulseboard.core.adapter.out.jdbc;

import com.pulseboard.core.application.port.out.ReadModel;
import com.pulseboard.core.domain.ActivityEvent;
import com.pulseboard.core.domain.SourceTypeStat;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcReadModel implements ReadModel {

    private final JdbcTemplate jdbc;

    public JdbcReadModel(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void apply(ActivityEvent e) {
        int updated = jdbc.update("UPDATE stats SET total = total + 1 WHERE source = ? AND type = ?", e.source(), e.type());
        if (updated == 0) {
            jdbc.update("INSERT INTO stats (source, type, total) VALUES (?,?,1)", e.source(), e.type());
        }
    }

    @Override
    public void clear() {
        jdbc.update("DELETE FROM stats");
    }

    @Override
    public List<SourceTypeStat> stats() {
        return jdbc.query("SELECT source, type, total FROM stats ORDER BY source, type",
                (rs, i) -> new SourceTypeStat(rs.getString("source"), rs.getString("type"), rs.getLong("total")));
    }
}
