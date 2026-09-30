package com.uelbosque.orderservice.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long id;
    private String productCode;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal itemSubtotal;

    public OrderItemResponse() {}

    public OrderItemResponse(Long id, String productCode, String productName, BigDecimal unitPrice, Integer quantity, BigDecimal itemSubtotal) {
        this.id = id;
        this.productCode = productCode;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.itemSubtotal = itemSubtotal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getItemSubtotal() { return itemSubtotal; }
    public void setItemSubtotal(BigDecimal itemSubtotal) { this.itemSubtotal = itemSubtotal; }
}
