package com.shopflow.monolith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderFlowTest {

    @Autowired
    MockMvc mvc;

    long userId;

    @BeforeEach
    void seed() throws Exception {
        String body = mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Asha\",\"email\":\"asha" + System.nanoTime() + "@example.com\"}"))
                .andReturn().getResponse().getContentAsString();
        userId = Long.parseLong(body.replaceAll(".*\"id\":(\\d+).*", "$1"));
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":\"CHEAP-" + userId + "\",\"name\":\"Pen\",\"price\":10}"));
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":\"LUX-" + userId + "\",\"name\":\"Watch\",\"price\":900}"));
    }

    @Test
    void affordableOrderIsPaid() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + userId + ",\"sku\":\"CHEAP-" + userId + "\",\"quantity\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.total").value(20));
    }

    @Test
    void orderOverLimitIsCancelled() throws Exception {
        String res = mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + userId + ",\"sku\":\"LUX-" + userId + "\",\"quantity\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andReturn().getResponse().getContentAsString();
        long orderId = Long.parseLong(res.replaceAll(".*\"id\":(\\d+).*", "$1"));
        mvc.perform(get("/api/orders/" + orderId)).andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void unknownUserIs404() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":99999,\"sku\":\"X\",\"quantity\":1}"))
                .andExpect(status().isNotFound());
    }
}
