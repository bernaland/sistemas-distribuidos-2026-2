package com.uelbosque.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:payment-insert", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "app.jwt.secret=test-secret-at-least-thirty-two-characters"})
class PaymentInsertTest {
    @Autowired PaymentRepository repository;
    @Test void aNewEntityWithExistingKeyCannotOverwritePayment() {
        var first=new Payment(new PaymentRequest("same-key",1L,PaymentRequest.Method.CASH,false),new BigDecimal("100.00"));
        repository.saveAndFlush(first);
        var second=new Payment(new PaymentRequest("same-key",2L,PaymentRequest.Method.CASH,false),new BigDecimal("200.00"));
        assertThrows(DataIntegrityViolationException.class,() -> repository.saveAndFlush(second));
        assertEquals(1L,repository.findById("same-key").orElseThrow().response().saleId());
    }
}
