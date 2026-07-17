package com.pulseboard.core.adapter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EventApiTest {

    @Autowired
    MockMvc mvc;

    private static String body(String id) {
        return """
            {"eventId":"%s","source":"billing","type":"invoice.failed","severity":"ERROR","payload":"{}"}
            """.formatted(id);
    }

    @Test
    void postThenDuplicateThenList() throws Exception {
        String id = UUID.randomUUID().toString();

        mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content(body(id)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("ACCEPTED"));
        mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content(body(id)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DUPLICATE"));
        mvc.perform(get("/api/events?limit=5")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(id));
    }

    @Test
    void invalidEventIsBadRequest() throws Exception {
        mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\":\"\",\"source\":\"s\",\"type\":\"t\"}"))
                .andExpect(status().isBadRequest());
    }
}
