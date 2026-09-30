package com.uelbosque.orderservice.dto;

import com.uelbosque.orderservice.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private String orderNumber;
    private String userId;
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxTotal;
    private BigDecimal discountTotal;
    private BigDecimal grandTotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderResponse() {}

    public OrderResponse(Long id, String orderNumber, String userId, String customerName,
                         String customerEmail, String shippingAddress, OrderStatus status,
                         List<OrderItemResponse> items, BigDecimal subtotal, BigDecimal taxRate,
                         BigDecimal taxTotal, BigDecimal discountTotal, BigDecimal grandTotal,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.status = status;
        this.items = items;
        this.subtotal = subtotal;
        this.taxRate = taxRate;
        this.taxTotal = taxTotal;
        this.discountTotal = discountTotal;
        this.grandTotal = grandTotal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTaxRate() { return taxRate; }
    public BigDecimal getTaxTotal() { return taxTotal; }
    public BigDecimal getDiscountTotal() { return discountTotal; }
    public BigDecimal getGrandTotal() { return grandTotal; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
