package com.uelbosque.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uelbosque.orderservice.dto.CreateOrderItemRequest;
import com.uelbosque.orderservice.dto.CreateOrderRequest;
import com.uelbosque.orderservice.dto.OrderItemResponse;
import com.uelbosque.orderservice.dto.OrderResponse;
import com.uelbosque.orderservice.dto.UpdateOrderStatusRequest;
import com.uelbosque.orderservice.exception.GlobalExceptionHandler;
import com.uelbosque.orderservice.exception.ResourceNotFoundException;
import com.uelbosque.orderservice.model.OrderStatus;
import com.uelbosque.orderservice.service.OrderService;
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

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderResponse mockOrderResponse() {
        OrderItemResponse item = new OrderItemResponse(1L, "P001", "Arroz",
                new BigDecimal("1000.00"), 2, new BigDecimal("2000.00"));
        return new OrderResponse(1L, "ORD-20260905-12345678", "user1", "Carlos Gomez",
                "carlos@example.com", "Calle 100", OrderStatus.PENDING, List.of(item),
                new BigDecimal("2000.00"), new BigDecimal("0.19"), new BigDecimal("380.00"),
                BigDecimal.ZERO, new BigDecimal("2380.00"), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/orders debe crear una orden y retornar 201")
    void shouldCreateOrder() throws Exception {
        CreateOrderItemRequest itemReq = new CreateOrderItemRequest("P001", "Arroz", new BigDecimal("1000.00"), 2);
        CreateOrderRequest req = new CreateOrderRequest("user1", "Carlos", "carlos@example.com",
                "Calle 100", BigDecimal.ZERO, List.of(itemReq));
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(mockOrderResponse());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("ORD-20260905-12345678"));
    }

    @Test
    @DisplayName("POST /api/orders con datos inválidos debe retornar 400")
    void shouldReturnBadRequestWhenInvalid() throws Exception {
        CreateOrderRequest req = new CreateOrderRequest("", "", "invalid-email", "", null, List.of());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.customerEmail").exists());
    }

    @Test
    @DisplayName("GET /api/orders/{id} debe retornar 200 cuando existe")
    void shouldGetOrderById() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(mockOrderResponse());

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/orders/{id} debe retornar 404 cuando no existe")
    void shouldReturn404WhenNotFound() throws Exception {
        when(orderService.getOrderById(99L)).thenThrow(new ResourceNotFoundException("Orden no encontrada: 99"));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Orden no encontrada: 99"));
    }

    @Test
    @DisplayName("GET /api/orders/number/{orderNumber} debe retornar 200")
    void shouldGetOrderByNumber() throws Exception {
        when(orderService.getOrderByOrderNumber("ORD-20260905-12345678")).thenReturn(mockOrderResponse());

        mockMvc.perform(get("/api/orders/number/ORD-20260905-12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-20260905-12345678"));
    }

    @Test
    @DisplayName("GET /api/orders/user/{userId} debe retornar 200")
    void shouldGetOrdersByUserId() throws Exception {
        when(orderService.getOrdersByUserId("user1")).thenReturn(List.of(mockOrderResponse()));

        mockMvc.perform(get("/api/orders/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user1"));
    }

    @Test
    @DisplayName("PUT /api/orders/{id}/status debe actualizar estado")
    void shouldUpdateStatus() throws Exception {
        UpdateOrderStatusRequest req = new UpdateOrderStatusRequest(OrderStatus.CONFIRMED);
        OrderResponse res = mockOrderResponse();
        res.setStatus(OrderStatus.CONFIRMED);
        when(orderService.updateOrderStatus(eq(1L), any(UpdateOrderStatusRequest.class))).thenReturn(res);

        mockMvc.perform(put("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("PUT /api/orders/{id}/cancel debe cancelar orden")
    void shouldCancelOrder() throws Exception {
        OrderResponse res = mockOrderResponse();
        res.setStatus(OrderStatus.CANCELLED);
        when(orderService.cancelOrder(1L)).thenReturn(res);

        mockMvc.perform(put("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
