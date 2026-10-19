package com.uelbosque.inventoryservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:stockrules", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "app.jwt.secret=test-secret-at-least-thirty-two-characters"})
class InventoryRulesTest {
    @Autowired InventoryService service;
    @Test void reservesIdempotentlyAndReleasesOnlyOnce() {
        service.adjust("1",10);
        var request=new ReservationRequest("order-1","1",3);
        service.reserve(request); service.reserve(request);
        assertEquals(7,service.get("1").available());
        service.finish("order-1",false); service.finish("order-1",false);
        assertEquals(10,service.get("1").available());
    }
    @Test void doesNotOversell() {
        service.adjust("2",1);
        assertThrows(org.springframework.web.server.ResponseStatusException.class,() -> service.reserve(new ReservationRequest("order-2","2",2)));
        assertEquals(1,service.get("2").available());
    }
    @Test void cannotReleaseCommittedStock() {
        service.adjust("3",5);
        service.reserve(new ReservationRequest("order-3","3",2));
        service.finish("order-3",true);
        assertThrows(org.springframework.web.server.ResponseStatusException.class,() -> service.finish("order-3",false));
        assertEquals(3,service.get("3").onHand());
    }
}
