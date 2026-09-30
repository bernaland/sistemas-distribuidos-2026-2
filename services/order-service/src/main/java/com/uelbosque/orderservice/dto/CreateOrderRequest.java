package com.uelbosque.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

public class CreateOrderRequest {

    @NotBlank(message = "El identificador de usuario es requerido")
    private String userId;

    @NotBlank(message = "El nombre del cliente es requerido")
    private String customerName;

    @NotBlank(message = "El correo electrónico del cliente es requerido")
    @Email(message = "El formato de correo electrónico no es válido")
    private String customerEmail;

    @NotBlank(message = "La dirección de envío es requerida")
    private String shippingAddress;

    private BigDecimal discountAmount;

    @NotEmpty(message = "La orden debe incluir al menos un producto")
    @Valid
    private List<CreateOrderItemRequest> items;

    public CreateOrderRequest() {}

    public CreateOrderRequest(String userId, String customerName, String customerEmail, String shippingAddress,
                              BigDecimal discountAmount, List<CreateOrderItemRequest> items) {
        this.userId = userId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.discountAmount = discountAmount;
        this.items = items;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public List<CreateOrderItemRequest> getItems() { return items; }
    public void setItems(List<CreateOrderItemRequest> items) { this.items = items; }
}
