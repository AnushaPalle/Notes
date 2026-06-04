package com.shopflow.monolith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.shopflow.contracts.OrderPlacedV1;
import com.shopflow.contracts.PaymentResultV1;
import com.shopflow.contracts.Topology;
import com.shopflow.monolith.billing.infra.PaymentResultConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

/** The order saga across the broker, with Kafka itself mocked out. */
@SpringBootTest(properties = {
        "shopflow.payments.mode=kafka",
        "spring.kafka.listener.auto-startup=false",
        "spring.kafka.admin.auto-create=false"
})
@AutoConfigureMockMvc
class OrderSagaTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    PaymentResultConsumer resultConsumer;

    @MockBean
    KafkaTemplate<String, Object> kafka;

    private long placeOrder(String sku) throws Exception {
        String user = mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Meera\",\"email\":\"m" + System.nanoTime() + "@example.com\"}"))
                .andReturn().getResponse().getContentAsString();
        long userId = Long.parseLong(user.replaceAll(".*\"id\":(\\d+).*", "$1"));
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":\"" + sku + "\",\"name\":\"Thing\",\"price\":10}"));
        String order = mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + userId + ",\"sku\":\"" + sku + "\",\"quantity\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(order.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void orderStaysPendingUntilPaymentResultArrivesThenIsPaid() throws Exception {
        long id = placeOrder("SAGA-OK");
        verify(kafka).send(eq(Topology.ORDER_PLACED_TOPIC), eq(String.valueOf(id)), any(OrderPlacedV1.class));

        resultConsumer.on(new PaymentResultV1(id, true, null));
        mvc.perform(get("/api/orders/" + id)).andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void failedPaymentCompensatesByCancellingTheOrder() throws Exception {
        long id = placeOrder("SAGA-FAIL");

        resultConsumer.on(new PaymentResultV1(id, false, "amount exceeds limit"));
        mvc.perform(get("/api/orders/" + id)).andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
