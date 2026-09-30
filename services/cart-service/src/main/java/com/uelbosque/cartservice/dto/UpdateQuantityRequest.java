package com.uelbosque.cartservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateQuantityRequest {

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer quantity;

    public UpdateQuantityRequest() {}

    public UpdateQuantityRequest(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
