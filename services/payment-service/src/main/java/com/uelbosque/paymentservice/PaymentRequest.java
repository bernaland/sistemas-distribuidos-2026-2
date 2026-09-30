package com.uelbosque.paymentservice;

import jakarta.validation.constraints.*;
public record PaymentRequest(@NotBlank @Size(max=100) String key,@NotNull @Positive Long saleId,
    @NotNull Method method,boolean simulateFailure) {
    public enum Method { CASH, TRANSFER, SIMULATED_CARD }
}
