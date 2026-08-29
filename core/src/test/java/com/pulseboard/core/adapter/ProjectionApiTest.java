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
class ProjectionApiTest {

    private static final String KEY = "test-key";
    private static final String ADMIN = "admin-key";

    @Autowired
    MockMvc mvc;
    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void clean() {
        jdbc.update("DELETE FROM stats");
        jdbc.update("DELETE FROM events");
    }

    private void send(String type) throws Exception {
        mvc.perform(post("/api/events").header("X-API-Key", KEY).contentType(MediaType.APPLICATION_JSON).content("""
                {"eventId":"%s","source":"shop","type":"%s","severity":"INFO"}
                """.formatted(UUID.randomUUID(), type))).andExpect(status().isAccepted());
    }

    private long events() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM events", Long.class);
    }

    @Test
    void statsAreProjectedFromIngestedEvents() throws Exception {
        send("order.created");
        send("order.created");
        send("order.paid");
        await().atMost(5, TimeUnit.SECONDS).until(() -> events() >= 3);

        mvc.perform(get("/api/stats").header("X-API-Key", KEY)).andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.type=='order.created')].total").value(2))
                .andExpect(jsonPath("$[?(@.type=='order.paid')].total").value(1));
    }

    @Test
    void replayRebuildsACorruptedReadModel() throws Exception {
        send("order.created");
        send("order.created");
        await().atMost(5, TimeUnit.SECONDS).until(() -> events() >= 2);
        jdbc.update("UPDATE stats SET total = 999");

        mvc.perform(post("/api/admin/replay").header("X-API-Key", ADMIN)).andExpect(status().isOk())
                .andExpect(jsonPath("$.replayed").isNumber());

        assertEquals(2L, jdbc.queryForObject("SELECT total FROM stats WHERE type='order.created'", Long.class));
    }
}
