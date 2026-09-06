package com.uelbosque.cartservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.util.Optional;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

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
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Cart() {}

    public Cart(String userId) {
        this.userId = userId;
        this.recalculateTotals();
    }

    public void addItem(String productCode, String productName, BigDecimal unitPrice, int quantity) {
        Optional<CartItem> existing = findItem(productCode);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            this.items.add(new CartItem(this, productCode, productName, unitPrice, quantity));
        }
        recalculateTotals();
    }

    public void updateQuantity(String productCode, int quantity) {
        if (quantity <= 0) {
            removeItem(productCode);
        } else {
            findItem(productCode).ifPresent(item -> item.setQuantity(quantity));
            recalculateTotals();
        }
    }

    public void removeItem(String productCode) {
        this.items.removeIf(item -> item.getProductCode().equalsIgnoreCase(productCode));
        recalculateTotals();
    }

    public void clear() {
        this.items.clear();
        this.discountTotal = BigDecimal.ZERO;
        recalculateTotals();
    }

    public void applyDiscount(BigDecimal discount) {
        this.discountTotal = discount != null && discount.compareTo(BigDecimal.ZERO) >= 0 ? discount : BigDecimal.ZERO;
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.subtotal = items.stream()
                .map(CartItem::getItemSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        this.taxTotal = this.subtotal.multiply(this.taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalBeforeDiscount = this.subtotal.add(this.taxTotal);
        this.grandTotal = totalBeforeDiscount.subtract(this.discountTotal).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        this.updatedAt = LocalDateTime.now();
    }

    public Optional<CartItem> findItem(String productCode) {
        return items.stream()
                .filter(i -> i.getProductCode().equalsIgnoreCase(productCode))
                .findFirst();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; recalculateTotals(); }

    public BigDecimal getTaxTotal() { return taxTotal; }
    public BigDecimal getDiscountTotal() { return discountTotal; }
    public BigDecimal getGrandTotal() { return grandTotal; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
