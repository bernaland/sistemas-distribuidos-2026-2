package com.uelbosque.adminservice;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class ReportServiceTest {
    private final ReportClient client=mock(ReportClient.class);
    private final ReportService service=new ReportService(client);
    @Test void groupsByCedulaAndCalculatesGrandTotal() {
        when(client.sales()).thenReturn(List.of(row("1","100.00"),row("2","200.00"),row("1","50.00")));
        var report=service.salesByCustomer();
        assertEquals(2,report.customers().size());
        assertEquals(new BigDecimal("150.00"),report.customers().get(0).totalSales());
        assertEquals(new BigDecimal("350.00"),report.grandTotal());
    }
    @Test void returnsEmptyReportWithoutInventingData() {
        when(client.sales()).thenReturn(List.of());
        assertTrue(service.salesByCustomer().customers().isEmpty());
        assertEquals(BigDecimal.ZERO,service.salesByCustomer().grandTotal());
    }
    @Test void doesNotHideUnavailableSalesService() {
        when(client.sales()).thenThrow(new org.springframework.web.client.ResourceAccessException("timeout"));
        assertThrows(org.springframework.web.client.RestClientException.class,service::salesByCustomer);
    }
    private ReportClient.SaleRow row(String cedula,String total) { return new ReportClient.SaleRow(cedula,"Cliente",new BigDecimal(total)); }
}
