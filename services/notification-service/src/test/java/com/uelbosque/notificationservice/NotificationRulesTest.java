package com.uelbosque.notificationservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:mailrules", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "app.jwt.secret=test-secret-at-least-thirty-two-characters"})
class NotificationRulesTest {
    @Autowired NotificationService service;
    @Test void queuesIdempotentlyAndNeverClaimsDeliveryWithoutSmtp() {
        var request=new NotificationRequest("mail-1","test@example.com","Venta","Confirmada");
        var first=service.queue(request);
        assertEquals(first,service.queue(request));
        assertThrows(org.springframework.web.server.ResponseStatusException.class,() -> service.send("mail-1"));
        assertEquals("PENDING",service.get("mail-1").status());
        assertEquals(0,service.get("mail-1").attempts());
    }
    @Test void rejectsKeyReuseForDifferentMessage() {
        service.queue(new NotificationRequest("mail-2","test@example.com","Venta","Confirmada"));
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
            () -> service.queue(new NotificationRequest("mail-2","other@example.com","Venta","Confirmada")));
    }
}
