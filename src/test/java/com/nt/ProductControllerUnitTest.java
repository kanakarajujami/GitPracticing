package com.nt;

import com.nt.controller.ProductController;
import com.nt.entity.Product;
import com.nt.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(new Product()));
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById_Found() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Mocked");
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mocked"));
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        when(productService.getProductById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/products/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setName("Created");
        when(productService.createProduct(any(Product.class))).thenReturn(product);
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Created"));
    }

    @Test
    void testUpdateProduct_Found() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Updated");
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(product);
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void testUpdateProduct_NotFound() throws Exception {
        when(productService.updateProduct(eq(2L), any(Product.class))).thenReturn(null);
        Product product = new Product();
        product.setName("NotFound");
        mockMvc.perform(put("/api/products/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProduct_Found() throws Exception {
        when(productService.deleteProduct(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProduct_NotFound() throws Exception {
        when(productService.deleteProduct(2L)).thenReturn(false);
        mockMvc.perform(delete("/api/products/2"))
                .andExpect(status().isNotFound());
    }
}

