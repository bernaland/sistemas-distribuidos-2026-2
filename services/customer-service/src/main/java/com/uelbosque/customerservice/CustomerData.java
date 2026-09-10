package com.uelbosque.customerservice;

public record CustomerData(@jakarta.validation.constraints.Pattern(regexp="[0-9]{1,20}") @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=255) String cedula,
    @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=255) String name,
    @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=255) String address,
    @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=255) String phone,
    @jakarta.validation.constraints.Email @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=255) String email) {}
