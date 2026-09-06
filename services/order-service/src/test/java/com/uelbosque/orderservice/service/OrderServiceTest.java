package com.uelbosque.orderservice.service;

import com.uelbosque.orderservice.dto.CreateOrderItemRequest;
import com.uelbosque.orderservice.dto.CreateOrderRequest;
import com.uelbosque.orderservice.dto.OrderResponse;
import com.uelbosque.orderservice.dto.UpdateOrderStatusRequest;
import com.uelbosque.orderservice.exception.InvalidOrderStateException;
import com.uelbosque.orderservice.exception.ResourceNotFoundException;
import com.uelbosque.orderservice.model.Order;
import com.uelbosque.orderservice.model.OrderStatus;
import com.uelbosque.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleOrder = new Order("ORD-20260905-12345678", "user1", "Carlos Gomez", "carlos@example.com", "Calle 100 #15-20");
        sampleOrder.setId(1L);
        sampleOrder.addItem("P001", "Arroz 1kg", new BigDecimal("1000.00"), 2);
    }

    @Test
    @DisplayName("Debe crear una orden y calcular subtotal, IVA 19% y total")
    void shouldCreateOrderSuccessfully() {
        CreateOrderItemRequest item = new CreateOrderItemRequest("P001", "Arroz 1kg", new BigDecimal("1000.00"), 2);
        CreateOrderRequest req = new CreateOrderRequest("user1", "Carlos Gomez", "carlos@example.com",
                "Calle 100 #15-20", new BigDecimal("100.00"), List.of(item));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse res = orderService.createOrder(req);

        assertNotNull(res);
        assertEquals("user1", res.getUserId());
        assertEquals(OrderStatus.PENDING, res.getStatus());
        assertEquals(new BigDecimal("2000.00"), res.getSubtotal());
        assertEquals(new BigDecimal("380.00"), res.getTaxTotal());
        assertEquals(new BigDecimal("100.00"), res.getDiscountTotal());
        assertEquals(new BigDecimal("2280.00"), res.getGrandTotal()); // (2000 + 380) - 100
    }

    @Test
    @DisplayName("Debe obtener orden por ID")
    void shouldGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        OrderResponse res = orderService.getOrderById(1L);

        assertNotNull(res);
        assertEquals(1L, res.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción si orden no existe por ID")
    void shouldThrowWhenOrderNotFoundById() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    @DisplayName("Debe obtener orden por número de orden")
    void shouldGetOrderByOrderNumber() {
        when(orderRepository.findByOrderNumber("ORD-20260905-12345678")).thenReturn(Optional.of(sampleOrder));

        OrderResponse res = orderService.getOrderByOrderNumber("ORD-20260905-12345678");

        assertNotNull(res);
        assertEquals("ORD-20260905-12345678", res.getOrderNumber());
    }

    @Test
    @DisplayName("Debe obtener órdenes por usuario")
    void shouldGetOrdersByUserId() {
        when(orderRepository.findByUserIdOrderByCreatedAtDesc("user1")).thenReturn(List.of(sampleOrder));

        List<OrderResponse> orders = orderService.getOrdersByUserId("user1");

        assertEquals(1, orders.size());
        assertEquals("user1", orders.get(0).getUserId());
    }

    @Test
    @DisplayName("Debe actualizar estado de una orden")
    void shouldUpdateOrderStatusSuccessfully() {
        UpdateOrderStatusRequest req = new UpdateOrderStatusRequest(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse res = orderService.updateOrderStatus(1L, req);

        assertEquals(OrderStatus.CONFIRMED, res.getStatus());
    }

    @Test
    @DisplayName("Debe cancelar una orden en estado PENDING")
    void shouldCancelOrderSuccessfully() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse res = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, res.getStatus());
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta cancelar orden ya entregada")
    void shouldThrowWhenCancellingDeliveredOrder() {
        sampleOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        assertThrows(InvalidOrderStateException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta cancelar orden ya cancelada")
    void shouldThrowWhenCancellingCancelledOrder() {
        sampleOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        assertThrows(InvalidOrderStateException.class, () -> orderService.cancelOrder(1L));
    }
}
