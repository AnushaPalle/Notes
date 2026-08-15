package com.pulseboard.core.adapter;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EventApiTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void clean() {
        jdbc.update("DELETE FROM stats");
        jdbc.update("DELETE FROM events");
    }

    private static String body(String id) {
        return """
            {"eventId":"%s","source":"billing","type":"invoice.failed","severity":"ERROR","payload":"{}"}
            """.formatted(id);
    }

    private void send(String body) throws Exception {
        mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.status").value("QUEUED"));
    }

    private long count(String sql, Object... args) {
        return jdbc.queryForObject(sql, Long.class, args);
    }

    @Test
    void eventIsQueuedThenStoredAndProjected() throws Exception {
        String id = UUID.randomUUID().toString();

        send(body(id));

        await().atMost(5, TimeUnit.SECONDS).until(() -> count("SELECT COUNT(*) FROM events WHERE event_id=?", id) == 1);
        mvc.perform(get("/api/events?limit=5")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(id));
        await().atMost(5, TimeUnit.SECONDS).until(() -> count("SELECT COALESCE(SUM(total),0) FROM stats") == 1);
    }

    @Test
    void sendingTheSameEventTwiceStoresItOnce() throws Exception {
        String id = UUID.randomUUID().toString();

        send(body(id));
        send(body(id));
        String marker = UUID.randomUUID().toString();
        send(body(marker));

        // single worker keeps order: once the marker is stored, both copies of `id` were processed
        await().atMost(5, TimeUnit.SECONDS).until(() -> count("SELECT COUNT(*) FROM events WHERE event_id=?", marker) == 1);
        assertEquals(1, count("SELECT COUNT(*) FROM events WHERE event_id=?", id));
        assertEquals(2, count("SELECT COALESCE(SUM(total),0) FROM stats"));
    }

    @Test
    void invalidEventIsRejectedSynchronously() throws Exception {
        mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\":\"\",\"source\":\"s\",\"type\":\"t\"}"))
                .andExpect(status().isBadRequest());
    }
}
