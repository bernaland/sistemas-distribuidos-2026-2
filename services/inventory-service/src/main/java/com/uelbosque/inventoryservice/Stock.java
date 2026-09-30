package com.uelbosque.inventoryservice;

import jakarta.persistence.*;
@Entity @Table(name="stock")
public class Stock {
    @Id private String productCode;
    private int available;
    private int reserved;
    @Version private Long version;
    protected Stock() {}
    public Stock(String code,int quantity) { this.productCode=code; this.available=quantity; }
    public void adjust(int onHand) {
        if (onHand<reserved) throw new IllegalArgumentException("Existencia inferior a las reservas");
        available=onHand-reserved;
    }
    public void reserve(int quantity) {
        if (quantity<1 || quantity>available) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"Stock insuficiente");
        available-=quantity; reserved+=quantity;
    }
    public void release(int quantity) { reserved-=quantity; available+=quantity; }
    public void commit(int quantity) { reserved-=quantity; }
    public StockResponse response() { return new StockResponse(productCode,available,reserved,available+reserved); }
}
