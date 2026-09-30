package com.uelbosque.adminservice;

import org.springframework.stereotype.Service;
import java.util.*;
import java.math.BigDecimal;
@Service
public class ReportService {
    private final ReportClient client;
    public ReportService(ReportClient client) { this.client=client; }
    public List<ReportClient.UserRow> users() { return client.users(); }
    public List<ReportClient.CustomerRow> customers() { return client.customers(); }
    public SalesReport salesByCustomer() {
        Map<String,SalesReport.Row> totals=new TreeMap<>();
        for (var sale:client.sales()) {
            var prior=totals.get(sale.customerCedula());
            BigDecimal total=sale.grandTotal().add(prior==null ? BigDecimal.ZERO : prior.totalSales());
            totals.put(sale.customerCedula(),new SalesReport.Row(sale.customerCedula(),sale.customerName(),total));
        }
        var rows=List.copyOf(totals.values());
        return new SalesReport(rows,rows.stream().map(SalesReport.Row::totalSales).reduce(BigDecimal.ZERO,BigDecimal::add));
    }
}
