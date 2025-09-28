package com.nt;

import com.nt.entity.Product;
import com.nt.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void setup() {
        productRepository.deleteAll();
    }

    @Test
    void testCreateProduct_Simple() throws Exception {
        Product product = new Product();
        product.setName("Simple");
        product.setPrice(1.0);
        product.setDescription("");
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateProduct_EmptyName() throws Exception {
        Product product = new Product();
        product.setName("");
        product.setPrice(2.0);
        product.setDescription("No name");
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql(
        statements = "INSERT INTO product (id, name, price, description) VALUES (100, 'InsertedProduct', 123.45, 'Inserted via SQL');",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void testGetAllProducts_Simple() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetProductById_Invalid() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProduct_Simple() throws Exception {
        Product product = new Product();
        product.setName("ToUpdate");
        product.setPrice(3.0);
        product.setDescription("Update");
        Product saved = productRepository.save(product);
        saved.setName("Updated");
        mockMvc.perform(put("/api/products/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteProduct_Simple() throws Exception {
        Product product = new Product();
        product.setName("ToDelete");
        product.setPrice(4.0);
        product.setDescription("Delete");
        Product saved = productRepository.save(product);
        mockMvc.perform(delete("/api/products/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}
