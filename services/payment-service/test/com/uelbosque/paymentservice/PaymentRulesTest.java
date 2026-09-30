package com.uelbosque.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:payrules", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "app.jwt.secret=test-secret-at-least-thirty-two-characters"})
class PaymentRulesTest {
    @Autowired PaymentService service;
    @MockBean SaleLookup sales;
    @Test void retryDoesNotChargeTwiceAndAmountComesFromSale() {
        when(sales.amount(1L)).thenReturn(new BigDecimal("4100.00"));
        var request=new PaymentRequest("pay-1",1L,PaymentRequest.Method.CASH,false);
        var first=service.pay(request);
        assertEquals(first,service.pay(request));
        assertEquals(new BigDecimal("4100.00"),first.amount());
        assertTrue(first.simulated());
        verify(sales,times(1)).amount(1L);
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
            () -> service.pay(new PaymentRequest("pay-2",1L,PaymentRequest.Method.CASH,false)));
    }
    @Test void recordsDeclinedSimulationWithoutMarkingPaid() {
        when(sales.amount(2L)).thenReturn(new BigDecimal("100.00"));
        assertEquals("DECLINED",service.pay(new PaymentRequest("declined",2L,PaymentRequest.Method.SIMULATED_CARD,true)).status());
        assertEquals("APPROVED",service.pay(new PaymentRequest("retry",2L,PaymentRequest.Method.CASH,false)).status());
    }
}
