package com.uelbosque.notificationservice;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;
@RestController @RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service=service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse queue(@Valid @RequestBody NotificationRequest request) { return service.queue(request); }
    @GetMapping public List<NotificationResponse> list() { return service.list(); }
    @GetMapping("/{key}") public NotificationResponse get(@PathVariable String key) { return service.get(key); }
    @PostMapping("/{key}/send") public NotificationResponse send(@PathVariable String key) { return service.send(key); }
}
