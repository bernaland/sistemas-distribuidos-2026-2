package com.uelbosque.supplierservice;

public record SupplierData(@jakarta.validation.constraints.Pattern(regexp="[0-9]{1,20}") @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=255) String nit,
    @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=255) String name,
    @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=255) String address,
    @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=255) String phone,
    @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=255) String city) {}
