package com.uelbosque.cartservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CartResponse {

    private Long id;
    private String userId;
    private List<CartItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxTotal;
    private BigDecimal discountTotal;
    private BigDecimal grandTotal;
    private LocalDateTime updatedAt;

    public CartResponse() {}

    public CartResponse(Long id, String userId, List<CartItemResponse> items, BigDecimal subtotal,
                        BigDecimal taxRate, BigDecimal taxTotal, BigDecimal discountTotal,
                        BigDecimal grandTotal, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.subtotal = subtotal;
        this.taxRate = taxRate;
        this.taxTotal = taxTotal;
        this.discountTotal = discountTotal;
        this.grandTotal = grandTotal;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getTaxTotal() { return taxTotal; }
    public void setTaxTotal(BigDecimal taxTotal) { this.taxTotal = taxTotal; }

    public BigDecimal getDiscountTotal() { return discountTotal; }
    public void setDiscountTotal(BigDecimal discountTotal) { this.discountTotal = discountTotal; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
