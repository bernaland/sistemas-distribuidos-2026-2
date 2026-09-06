package com.uelbosque.cartservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uelbosque.cartservice.dto.AddItemRequest;
import com.uelbosque.cartservice.dto.ApplyDiscountRequest;
import com.uelbosque.cartservice.dto.CartItemResponse;
import com.uelbosque.cartservice.dto.CartResponse;
import com.uelbosque.cartservice.dto.UpdateQuantityRequest;
import com.uelbosque.cartservice.exception.GlobalExceptionHandler;
import com.uelbosque.cartservice.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import(GlobalExceptionHandler.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    private CartResponse mockCartResponse() {
        CartItemResponse item = new CartItemResponse(1L, "P001", "Arroz",
                new BigDecimal("1000.00"), 2, new BigDecimal("2000.00"));
        return new CartResponse(1L, "user123", List.of(item),
                new BigDecimal("2000.00"), new BigDecimal("0.19"),
                new BigDecimal("380.00"), BigDecimal.ZERO,
                new BigDecimal("2380.00"), LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/cart/{userId} debe retornar 200 y el carrito")
    void shouldGetCart() throws Exception {
        when(cartService.getOrCreateCart("user123")).thenReturn(mockCartResponse());

        mockMvc.perform(get("/api/cart/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.grandTotal").value(2380.00));
    }

    @Test
    @DisplayName("POST /api/cart/{userId}/items debe agregar item y retornar 200")
    void shouldAddItem() throws Exception {
        AddItemRequest req = new AddItemRequest("P001", "Arroz", new BigDecimal("1000.00"), 2);
        when(cartService.addItem(eq("user123"), any(AddItemRequest.class))).thenReturn(mockCartResponse());

        mockMvc.perform(post("/api/cart/user123/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productCode").value("P001"));
    }

    @Test
    @DisplayName("POST /api/cart/{userId}/items con datos inválidos debe retornar 400")
    void shouldReturnBadRequestWhenAddItemInvalid() throws Exception {
        AddItemRequest req = new AddItemRequest("", "", new BigDecimal("-10.00"), 0);

        mockMvc.perform(post("/api/cart/user123/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.productCode").exists());
    }

    @Test
    @DisplayName("PUT /api/cart/{userId}/items/{productCode} debe actualizar cantidad")
    void shouldUpdateQuantity() throws Exception {
        UpdateQuantityRequest req = new UpdateQuantityRequest(5);
        when(cartService.updateItemQuantity(eq("user123"), eq("P001"), any(UpdateQuantityRequest.class)))
                .thenReturn(mockCartResponse());

        mockMvc.perform(put("/api/cart/user123/items/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/cart/{userId}/items/{productCode} debe remover item")
    void shouldRemoveItem() throws Exception {
        when(cartService.removeItem("user123", "P001")).thenReturn(mockCartResponse());

        mockMvc.perform(delete("/api/cart/user123/items/P001"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/cart/{userId} debe vaciar carrito")
    void shouldClearCart() throws Exception {
        when(cartService.clearCart("user123")).thenReturn(mockCartResponse());

        mockMvc.perform(delete("/api/cart/user123"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/cart/{userId}/discount debe aplicar descuento")
    void shouldApplyDiscount() throws Exception {
        ApplyDiscountRequest req = new ApplyDiscountRequest(new BigDecimal("100.00"));
        when(cartService.applyDiscount(eq("user123"), any(ApplyDiscountRequest.class)))
                .thenReturn(mockCartResponse());

        mockMvc.perform(post("/api/cart/user123/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
