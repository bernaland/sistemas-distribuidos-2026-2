package com.uelbosque.orderservice;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Embeddable
public class SaleLine {
    private String productCode;
    private String productName;
    private int quantity;
    @Column(precision=18,scale=2) private BigDecimal unitPrice;
    @Column(precision=5,scale=2) private BigDecimal ivaRate;
    @Column(precision=18,scale=2) private BigDecimal subtotal;
    @Column(precision=18,scale=2) private BigDecimal tax;
    protected SaleLine() {}
    public SaleLine(SaleDirectory.Product product, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Cantidad invalida");
        this.productCode=product.code(); this.productName=product.name(); this.quantity=quantity;
        this.unitPrice=product.salePrice().setScale(2,RoundingMode.UNNECESSARY);
        this.ivaRate=product.ivaRate().setScale(2,RoundingMode.UNNECESSARY);
        this.subtotal=unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2,RoundingMode.HALF_UP);
        this.tax=subtotal.multiply(ivaRate).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);
    }
    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getIvaRate() { return ivaRate; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTax() { return tax; }
}
