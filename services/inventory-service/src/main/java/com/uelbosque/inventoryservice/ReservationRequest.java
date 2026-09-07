package com.uelbosque.inventoryservice;

import jakarta.validation.constraints.*;
public record ReservationRequest(@NotBlank @Size(max=100) String key,@NotBlank String productCode,@Min(1) @Max(1000000) int quantity) {}
