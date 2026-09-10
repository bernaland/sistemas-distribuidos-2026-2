package com.uelbosque.adminservice;

import java.math.BigDecimal;
import java.util.List;
public record SalesReport(List<Row> customers,BigDecimal grandTotal) {
    public record Row(String cedula,String name,BigDecimal totalSales) {}
}
