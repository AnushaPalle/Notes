package com.pulseboard.core.adapter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pulseboard.core.adapter.in.web.security.HmacVerifier;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    MockMvc mvc;

    private static String webhookBody() {
        return "{\"eventId\":\"" + UUID.randomUUID() + "\",\"type\":\"push\",\"severity\":\"INFO\",\"payload\":\"x\"}";
    }

    @Test
    void requestsWithoutApiKeyAreUnauthorized() throws Exception {
        mvc.perform(get("/api/events")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/events").header("X-API-Key", "nope")).andExpect(status().isUnauthorized());
    }

    @Test
    void normalKeyCannotUseAdminEndpoints() throws Exception {
        mvc.perform(post("/api/admin/replay").header("X-API-Key", "test-key")).andExpect(status().isForbidden());
        mvc.perform(post("/api/admin/replay").header("X-API-Key", "admin-key")).andExpect(status().isOk());
    }

    @Test
    void webhookWithValidSignatureIsAccepted() throws Exception {
        String body = webhookBody();
        String sig = HmacVerifier.sign("whsec_test", body.getBytes(StandardCharsets.UTF_8));

        mvc.perform(post("/api/webhooks/github").header("X-Signature", sig)
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isAccepted());
    }

    @Test
    void webhookWithTamperedBodyOrMissingSignatureIsRejected() throws Exception {
        String body = webhookBody();
        String sig = HmacVerifier.sign("whsec_test", body.getBytes(StandardCharsets.UTF_8));

        mvc.perform(post("/api/webhooks/github").header("X-Signature", sig)
                .contentType(MediaType.APPLICATION_JSON).content(body.replace("push", "pull")))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/webhooks/github").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }
}
