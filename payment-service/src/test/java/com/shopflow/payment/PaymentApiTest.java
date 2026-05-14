package com.shopflow.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    PaymentService service;

    @Test
    void retriesWithSameKeyReturnSamePayment() {
        var first = service.process("k-1", 1, new BigDecimal("10"));
        var second = service.process("k-1", 1, new BigDecimal("10"));
        assertThat(second).isEqualTo(first);
    }

    @Test
    void declinesAmountsOverTheLimit() throws Exception {
        mvc.perform(post("/payments").header("Idempotency-Key", "k-2").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":2,\"amount\":5000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DECLINED"));
    }

    @Test
    void requiresIdempotencyKey() throws Exception {
        mvc.perform(post("/payments").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":3,\"amount\":5}"))
                .andExpect(status().isBadRequest());
    }
}
