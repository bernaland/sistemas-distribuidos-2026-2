package com.uelbosque.notificationservice;

import jakarta.validation.constraints.*;
public record NotificationRequest(@NotBlank @Size(max=100) String key,@NotBlank @Email String recipient,
    @NotBlank @Size(max=150) String subject,@NotBlank @Size(max=5000) String body) {}
