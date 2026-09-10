package com.uelbosque.orderservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class SaleServiceTest {
    @Mock SaleRepository repository;
    @Mock SaleDirectory directory;
    @InjectMocks SaleService service;
    @Test void usesCatalogPricesAndIndividualTaxRates() {
        when(directory.customer("123")).thenReturn(new SaleDirectory.Customer("123","Cliente"));
        when(directory.operator("admin")).thenReturn(new SaleDirectory.Operator("456","admin",true));
        when(directory.product("1")).thenReturn(product("1","1000","0"));
        when(directory.product("2")).thenReturn(product("2","2000","5"));
        when(repository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var request=new SaleRequest("123",List.of(new SaleRequest.Item("1",2),new SaleRequest.Item("2",1)));
        var sale=service.create("key",request,"admin");
        assertEquals(new BigDecimal("4000.00"),sale.subtotal());
        assertEquals(new BigDecimal("100.00"),sale.taxTotal());
        assertEquals(new BigDecimal("4100.00"),sale.grandTotal());
        assertEquals("456",sale.userCedula());
    }
    @Test void refusesInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new SaleLine(product("1","1000","19"),-1));
    }
    @Test void dependencyFailureDoesNotSaveSale() {
        when(directory.customer("404")).thenThrow(new IllegalArgumentException("No existe"));
        assertThrows(IllegalArgumentException.class,() -> service.create("key",new SaleRequest("404",List.of()),"admin"));
        verify(repository,never()).saveAndFlush(any());
    }
    private SaleDirectory.Product product(String code,String price,String iva) {
        return new SaleDirectory.Product(code,"Producto",new BigDecimal(price),new BigDecimal(iva),true);
    }
}
