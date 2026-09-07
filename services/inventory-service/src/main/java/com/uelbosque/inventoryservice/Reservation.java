package com.uelbosque.inventoryservice;

import jakarta.persistence.*;
@Entity @Table(name="reservations")
public class Reservation {
    @Version private Long version;
    @Id private String reservationKey;
    private String productCode;
    private int quantity;
    private String status="RESERVED";
    protected Reservation() {}
    public Reservation(ReservationRequest request) {
        this.reservationKey=request.key(); this.productCode=request.productCode(); this.quantity=request.quantity();
    }
    public boolean matches(ReservationRequest request) { return productCode.equals(request.productCode()) && quantity==request.quantity(); }
    public String getProductCode() { return productCode; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public void complete(String status) { this.status=status; }
    public ReservationResponse response() { return new ReservationResponse(reservationKey,productCode,quantity,status); }
}
