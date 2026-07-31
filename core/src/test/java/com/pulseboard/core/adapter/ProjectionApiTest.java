package com.pulseboard.core.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
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
        mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content("""
                {"eventId":"%s","source":"shop","type":"%s","severity":"INFO"}
                """.formatted(UUID.randomUUID(), type))).andExpect(status().isCreated());
    }

    @Test
    void statsAreProjectedFromIngestedEvents() throws Exception {
        send("order.created");
        send("order.created");
        send("order.paid");

        mvc.perform(get("/api/stats")).andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.type=='order.created')].total").value(2))
                .andExpect(jsonPath("$[?(@.type=='order.paid')].total").value(1));
    }

    @Test
    void replayRebuildsACorruptedReadModel() throws Exception {
        send("order.created");
        send("order.created");
        jdbc.update("UPDATE stats SET total = 999");

        mvc.perform(post("/api/admin/replay")).andExpect(status().isOk())
                .andExpect(jsonPath("$.replayed").value(2));

        assertEquals(2L, jdbc.queryForObject("SELECT total FROM stats WHERE type='order.created'", Long.class));
    }
}
