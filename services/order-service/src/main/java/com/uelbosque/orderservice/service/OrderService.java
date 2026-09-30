package com.uelbosque.orderservice.service;

import com.uelbosque.orderservice.dto.CreateOrderItemRequest;
import com.uelbosque.orderservice.dto.CreateOrderRequest;
import com.uelbosque.orderservice.dto.OrderItemResponse;
import com.uelbosque.orderservice.dto.OrderResponse;
import com.uelbosque.orderservice.dto.UpdateOrderStatusRequest;
import com.uelbosque.orderservice.exception.ResourceNotFoundException;
import com.uelbosque.orderservice.model.Order;
import com.uelbosque.orderservice.model.OrderItem;
import com.uelbosque.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        String orderNum = generateOrderNumber();
        Order order = new Order(orderNum, request.getUserId(), request.getCustomerName(),
                request.getCustomerEmail(), request.getShippingAddress());
        if (request.getDiscountAmount() != null) {
            order.setDiscountTotal(request.getDiscountAmount());
        }
        for (CreateOrderItemRequest itemReq : request.getItems()) {
            order.addItem(itemReq.getProductCode(), itemReq.getProductName(),
                    itemReq.getUnitPrice(), itemReq.getQuantity());
        }
        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con número: " + orderNumber));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        order.updateStatus(request.getStatus());
        return toResponse(orderRepository.save(order));
    }

    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        order.cancel();
        return toResponse(orderRepository.save(order));
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + datePart + "-" + randPart;
    }

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse).toList();
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getUserId(),
                order.getCustomerName(), order.getCustomerEmail(), order.getShippingAddress(),
                order.getStatus(), items, order.getSubtotal(), order.getTaxRate(),
                order.getTaxTotal(), order.getDiscountTotal(), order.getGrandTotal(),
                order.getCreatedAt(), order.getUpdatedAt());
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(item.getId(), item.getProductCode(),
                item.getProductName(), item.getUnitPrice(),
                item.getQuantity(), item.getItemSubtotal());
    }
}
