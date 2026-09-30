package com.uelbosque.notificationservice;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Component
public class MailDelivery {
    private final ObjectProvider<JavaMailSender> sender;
    private final boolean enabled;
    private final String from;
    public MailDelivery(ObjectProvider<JavaMailSender> sender,@Value("${app.mail.enabled:false}") boolean enabled,
        @Value("${app.mail.from:tienda@example.com}") String from) { this.sender=sender; this.enabled=enabled; this.from=from; }
    public void checkEnabled() {
        if (!enabled || sender.getIfAvailable()==null) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"SMTP no configurado; notificacion pendiente");
    }
    public void send(NotificationResponse notification) {
        var message=new SimpleMailMessage();
        message.setFrom(from); message.setTo(notification.recipient());
        message.setSubject(notification.subject()); message.setText(notification.body());
        sender.getObject().send(message);
    }
}
