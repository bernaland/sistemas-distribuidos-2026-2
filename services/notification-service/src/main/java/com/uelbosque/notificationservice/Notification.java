package com.uelbosque.notificationservice;

import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="notifications")
public class Notification {
    @Version private Long version;
    @Id private String notificationKey;
    private String recipient;
    private String subject;
    @Column(length=5000) private String body;
    private String status="PENDING";
    private int attempts;
    private Instant createdAt=Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    protected Notification() {}
    public Notification(NotificationRequest request) {
        this.notificationKey=request.key(); this.recipient=request.recipient(); this.subject=request.subject(); this.body=request.body();
    }
    public boolean matches(NotificationRequest request) {
        return recipient.equals(request.recipient()) && subject.equals(request.subject()) && body.equals(request.body());
    }
    public boolean sent() { return "SENT".equals(status); }
    public void result(boolean success) { attempts++; status=success ? "SENT" : "FAILED"; }
    public NotificationResponse response() { return new NotificationResponse(notificationKey,recipient,subject,body,status,attempts,createdAt); }
}
