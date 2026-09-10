package com.uelbosque.orderservice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record SaleRequest(@NotBlank @Pattern(regexp="[0-9]{1,20}") String customerCedula,
    @NotNull @Size(min=1,max=3) List<@NotNull @Valid Item> items) {
    public record Item(@NotBlank String productCode, @NotNull @Min(1) @Max(1000000) Integer quantity) {}
}
