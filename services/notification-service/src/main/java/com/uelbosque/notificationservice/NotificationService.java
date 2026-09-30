package com.uelbosque.notificationservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import java.util.List;
@Service @Transactional
public class NotificationService {
    private final NotificationRepository repository;
    private final MailDelivery delivery;
    public NotificationService(NotificationRepository repository,MailDelivery delivery) { this.repository=repository; this.delivery=delivery; }
    public NotificationResponse queue(NotificationRequest request) {
        var previous=repository.findById(request.key());
        if (previous.isPresent()) {
            if (!previous.get().matches(request)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Clave usada por otra notificacion");
            return previous.get().response();
        }
        return repository.saveAndFlush(new Notification(request)).response();
    }
    public NotificationResponse send(String key) {
        Notification notification=repository.locked(key).orElseThrow(() -> missing());
        if (notification.sent()) return notification.response();
        delivery.checkEnabled();
        try { delivery.send(notification.response()); notification.result(true); }
        catch (MailException e) { notification.result(false); }
        return notification.response();
    }
    @Transactional(readOnly=true)
    public List<NotificationResponse> list() { return repository.findAll().stream().map(Notification::response).toList(); }
    @Transactional(readOnly=true)
    public NotificationResponse get(String key) { return repository.findById(key).orElseThrow(() -> missing()).response(); }
    private ResponseStatusException missing() { return new ResponseStatusException(HttpStatus.NOT_FOUND,"Notificacion inexistente"); }
}
