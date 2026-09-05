package com.pulseboard.core.adapter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class StreamApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void streamOpensWithHeaderKey() throws Exception {
        mvc.perform(get("/api/stream").header("X-API-Key", "test-key")).andExpect(request().asyncStarted());
    }

    @Test
    void streamOpensWithQueryKeyBecauseEventSourceCannotSetHeaders() throws Exception {
        mvc.perform(get("/api/stream?apiKey=test-key")).andExpect(request().asyncStarted());
    }

    @Test
    void streamWithoutKeyIsUnauthorized() throws Exception {
        mvc.perform(get("/api/stream")).andExpect(status().isUnauthorized());
    }

    @Test
    void queryKeyIsNotAcceptedOutsideTheStreamEndpoint() throws Exception {
        mvc.perform(get("/api/events?apiKey=test-key")).andExpect(status().isUnauthorized());
    }
}
