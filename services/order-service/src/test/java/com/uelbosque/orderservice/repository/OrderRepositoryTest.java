package com.uelbosque.orderservice.repository;

import com.uelbosque.orderservice.model.Order;
import com.uelbosque.orderservice.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        Order order = new Order("ORD-001", "user1", "Carlos Gomez", "carlos@example.com", "Calle 100");
        order.addItem("P001", "Arroz 1kg", new BigDecimal("1000.00"), 2);
        orderRepository.save(order);
    }

    @Test
    @DisplayName("Debe buscar orden por orderNumber con items")
    void shouldFindByOrderNumber() {
        Optional<Order> found = orderRepository.findByOrderNumber("ORD-001");

        assertTrue(found.isPresent());
        assertEquals("user1", found.get().getUserId());
        assertEquals(1, found.get().getItems().size());
        assertEquals("P001", found.get().getItems().get(0).getProductCode());
    }

    @Test
    @DisplayName("Debe listar órdenes por userId")
    void shouldFindByUserId() {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc("user1");

        assertEquals(1, orders.size());
        assertEquals("ORD-001", orders.get(0).getOrderNumber());
    }

    @Test
    @DisplayName("Debe listar órdenes por status")
    void shouldFindByStatus() {
        List<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(OrderStatus.PENDING);

        assertEquals(1, orders.size());
        assertEquals(OrderStatus.PENDING, orders.get(0).getStatus());
    }
}
