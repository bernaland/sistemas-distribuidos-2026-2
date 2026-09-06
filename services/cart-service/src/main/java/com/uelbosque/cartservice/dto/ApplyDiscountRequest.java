package com.uelbosque.cartservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ApplyDiscountRequest {

    @NotNull(message = "El monto de descuento es requerido")
    @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
    private BigDecimal discountAmount;

    public ApplyDiscountRequest() {}

    public ApplyDiscountRequest(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
}
