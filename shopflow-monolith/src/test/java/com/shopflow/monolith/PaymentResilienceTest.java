package com.shopflow.monolith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** payment-service is unreachable: the order must be cancelled gracefully, not fail with a 500. */
@SpringBootTest(properties = {
        "shopflow.payments.mode=rest",
        "shopflow.payments.url=http://localhost:1",
        "resilience4j.retry.instances.payments.wait-duration=10ms"
})
@AutoConfigureMockMvc
class PaymentResilienceTest {

    @Autowired
    MockMvc mvc;

    @Test
    void orderIsCancelledWhenPaymentServiceIsDown() throws Exception {
        String user = mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ravi\",\"email\":\"ravi@example.com\"}"))
                .andReturn().getResponse().getContentAsString();
        long userId = Long.parseLong(user.replaceAll(".*\"id\":(\\d+).*", "$1"));
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":\"RES-1\",\"name\":\"Cable\",\"price\":5}"));

        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + userId + ",\"sku\":\"RES-1\",\"quantity\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
