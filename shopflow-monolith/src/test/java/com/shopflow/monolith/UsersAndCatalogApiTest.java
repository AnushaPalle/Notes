package com.shopflow.monolith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UsersAndCatalogApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void createsAndFetchesProduct() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"BK-1\",\"name\":\"Book\",\"price\":12.50}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/products/BK-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Book"));
    }

    @Test
    void unknownProductIs404() throws Exception {
        mvc.perform(get("/api/products/NOPE")).andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidUser() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest());
    }
}
