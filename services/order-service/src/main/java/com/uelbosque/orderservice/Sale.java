package com.uelbosque.orderservice;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
@Entity @Table(name="sales")
public class Sale {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true) private String idempotencyKey;
    @Column(nullable=false,length=2000) private String fingerprint;
    @Column(nullable=false) private String customerCedula;
    private String customerName;
    @Column(nullable=false) private String userCedula;
    private String username;
    private Instant createdAt=Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    @ElementCollection @CollectionTable(name="sale_details",joinColumns=@JoinColumn(name="sale_id"))
    @OrderColumn(name="line_number") private List<SaleLine> items=new ArrayList<>();
    @Column(precision=18,scale=2) private BigDecimal subtotal;
    @Column(precision=18,scale=2) private BigDecimal taxTotal;
    @Column(precision=18,scale=2) private BigDecimal grandTotal;
    protected Sale() {}
    public Sale(String key, String fingerprint, SaleDirectory.Customer customer, SaleDirectory.Operator user, List<SaleLine> lines) {
        this.idempotencyKey=key; this.fingerprint=fingerprint;
        this.customerCedula=customer.cedula(); this.customerName=customer.name();
        this.userCedula=user.cedula(); this.username=user.username(); this.items.addAll(lines);
        this.subtotal=lines.stream().map(SaleLine::getSubtotal).reduce(BigDecimal.ZERO,BigDecimal::add);
        this.taxTotal=lines.stream().map(SaleLine::getTax).reduce(BigDecimal.ZERO,BigDecimal::add);
        this.grandTotal=subtotal.add(taxTotal);
    }
    public boolean matches(String fingerprint) { return this.fingerprint.equals(fingerprint); }
    public SaleResponse response() {
        return new SaleResponse(id,customerCedula,customerName,userCedula,username,createdAt,
            items.stream().map(SaleResponse.Line::from).toList(),subtotal,taxTotal,grandTotal);
    }
}
