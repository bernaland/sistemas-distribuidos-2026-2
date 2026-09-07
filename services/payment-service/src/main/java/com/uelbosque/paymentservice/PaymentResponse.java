package com.uelbosque.paymentservice;

import java.math.BigDecimal;
import java.time.Instant;
public record PaymentResponse(String key,Long saleId,BigDecimal amount,String status,PaymentRequest.Method method,
    boolean simulated,Instant createdAt) {}
