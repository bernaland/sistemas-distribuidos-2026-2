package com.uelbosque.orderservice.dto;

import com.uelbosque.orderservice.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {

    @NotNull(message = "El nuevo estado es requerido")
    private OrderStatus status;

    public UpdateOrderStatusRequest() {}

    public UpdateOrderStatusRequest(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
