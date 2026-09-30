package com.uelbosque.paymentservice;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="payments")
public class Payment {
    @Version private Long version;
    @Id private String paymentKey;
    private Long saleId;
    @Column(unique=true) private Long paidSaleId;
    @Column(precision=18,scale=2) private BigDecimal amount;
    private String status;
    @Enumerated(EnumType.STRING) private PaymentRequest.Method method;
    private boolean simulated=true;
    private Instant createdAt=Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    protected Payment() {}
    public Payment(PaymentRequest request,BigDecimal amount) {
        this.paymentKey=request.key(); this.saleId=request.saleId(); this.method=request.method(); this.amount=amount;
        this.status=request.simulateFailure() ? "DECLINED" : "APPROVED";
        if (!request.simulateFailure()) this.paidSaleId=request.saleId();
    }
    public boolean matches(PaymentRequest request) {
        return saleId.equals(request.saleId()) && method==request.method() && status.equals(request.simulateFailure() ? "DECLINED" : "APPROVED");
    }
    public PaymentResponse response() { return new PaymentResponse(paymentKey,saleId,amount,status,method,simulated,createdAt); }
}
