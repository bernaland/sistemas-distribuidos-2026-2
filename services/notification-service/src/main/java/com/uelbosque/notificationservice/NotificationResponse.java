package com.uelbosque.notificationservice;

import java.time.Instant;
public record NotificationResponse(String key,String recipient,String subject,String body,String status,int attempts,Instant createdAt) {}
