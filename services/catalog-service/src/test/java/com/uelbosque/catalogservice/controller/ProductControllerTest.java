package com.uelbosque.catalogservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uelbosque.catalogservice.dto.ProductRequestDto;
import com.uelbosque.catalogservice.dto.ProductResponseDto;
import com.uelbosque.catalogservice.exception.ResourceNotFoundException;
import com.uelbosque.catalogservice.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService service;

    @Test
    @DisplayName("GET /api/products debe retornar 200 y lista de productos")
    void shouldReturnAllProducts() throws Exception {
        ProductResponseDto dto = new ProductResponseDto(1L, "P001", "Arroz", "Desc",
                new BigDecimal("3000.00"), new BigDecimal("4000.00"),
                new BigDecimal("19.00"), "Granos", "http://img.jpg", true);

        when(service.searchProducts(null, null)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("P001"))
                .andExpect(jsonPath("$[0].name").value("Arroz"));
    }

    @Test
    @DisplayName("GET /api/products/{id} debe retornar 200 cuando el producto existe")
    void shouldReturnProductById() throws Exception {
        ProductResponseDto dto = new ProductResponseDto(1L, "P001", "Arroz", "Desc",
                new BigDecimal("3000.00"), new BigDecimal("4000.00"),
                new BigDecimal("19.00"), "Granos", "http://img.jpg", true);

        when(service.getProductById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.code").value("P001"));
    }

    @Test
    @DisplayName("GET /api/products/{id} debe retornar 404 cuando el producto no existe")
    void shouldReturn404WhenNotFound() throws Exception {
        when(service.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con ID: 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/products debe retornar 201 y el producto creado")
    void shouldCreateProduct() throws Exception {
        ProductRequestDto req = new ProductRequestDto();
        req.setCode("P002");
        req.setName("Frijol");
        req.setDescription("Frijol bola roja");
        req.setPurchasePrice(new BigDecimal("4000.00"));
        req.setSalePrice(new BigDecimal("5500.00"));
        req.setIvaRate(new BigDecimal("0.00"));

        ProductResponseDto res = new ProductResponseDto(2L, "P002", "Frijol", "Frijol bola roja",
                new BigDecimal("4000.00"), new BigDecimal("5500.00"),
                new BigDecimal("0.00"), null, null, true);

        when(service.createProduct(any(ProductRequestDto.class))).thenReturn(res);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("P002"));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} debe retornar 204 No Content")
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
