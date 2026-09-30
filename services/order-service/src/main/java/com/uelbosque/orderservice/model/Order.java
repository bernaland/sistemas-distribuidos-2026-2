package com.uelbosque.orderservice.model;

import com.uelbosque.orderservice.exception.InvalidOrderStateException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("0.19");

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Order() {}

    public Order(String orderNumber, String userId, String customerName, String customerEmail, String shippingAddress) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(String productCode, String productName, BigDecimal unitPrice, int quantity) {
        this.items.add(new OrderItem(this, productCode, productName, unitPrice, quantity));
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.subtotal = items.stream()
                .map(OrderItem::getItemSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        this.taxTotal = this.subtotal.multiply(this.taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalBeforeDiscount = this.subtotal.add(this.taxTotal);
        this.grandTotal = totalBeforeDiscount.subtract(this.discountTotal).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(OrderStatus newStatus) {
        if (this.status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("No se puede cambiar el estado de una orden entregada");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("No se puede cambiar el estado de una orden cancelada");
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("No se puede cancelar una orden que ya fue entregada");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("La orden ya se encuentra cancelada");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
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

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; recalculateTotals(); }

    public BigDecimal getTaxTotal() { return taxTotal; }
    public BigDecimal getDiscountTotal() { return discountTotal; }
    public void setDiscountTotal(BigDecimal discountTotal) { this.discountTotal = discountTotal; recalculateTotals(); }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
