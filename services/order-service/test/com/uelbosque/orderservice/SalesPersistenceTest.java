package com.uelbosque.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties="spring.datasource.url=jdbc:h2:mem:sales-persistence;DB_CLOSE_DELAY=-1")
class SalesPersistenceTest {
    @Autowired SaleService service;
    @MockBean SaleDirectory directory;

    @Test void persistsDetailsAndReturnsSameSaleOnRetry() {
        references();
        var request=new SaleRequest("123",List.of(new SaleRequest.Item("1",2)));
        var first=service.create("persistent-key",request,"admin");
        assertNotNull(first.id());
        assertEquals(first,service.get(first.id()));
        assertEquals(first,service.create("persistent-key",request,"admin"));
        assertEquals(new BigDecimal("2100.00"),first.grandTotal());
        verify(directory,times(1)).product("1");
    }
    @Test void rejectsReusingKeyWithDifferentSale() {
        references();
        service.create("conflict-key",new SaleRequest("123",List.of(new SaleRequest.Item("1",1))),"admin");
        var different=new SaleRequest("123",List.of(new SaleRequest.Item("1",2)));
        assertThrows(ResponseStatusException.class,() -> service.create("conflict-key",different,"admin"));
    }
    private void references() {
        when(directory.customer("123")).thenReturn(new SaleDirectory.Customer("123","Cliente"));
        when(directory.operator("admin")).thenReturn(new SaleDirectory.Operator("456","admin",true));
        when(directory.product("1")).thenReturn(new SaleDirectory.Product("1","Arroz",new BigDecimal("1000.00"),new BigDecimal("5"),true));
    }
}
