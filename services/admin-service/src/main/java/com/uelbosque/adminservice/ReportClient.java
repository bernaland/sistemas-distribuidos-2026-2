package com.uelbosque.adminservice;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;
import java.math.BigDecimal;
@Component
public class ReportClient {
    private final RestTemplate http;
    private final String users;
    private final String customers;
    private final String orders;
    public ReportClient(RestTemplate http,@Value("${services.users:http://localhost:8081}") String users,
        @Value("${services.customers:http://localhost:8089}") String customers,@Value("${services.orders:http://localhost:8085}") String orders) {
        this.http=http; this.users=users; this.customers=customers; this.orders=orders;
    }
    public List<UserRow> users() { return required(http.getForObject(users+"/api/users",UserRow[].class)); }
    public List<CustomerRow> customers() { return required(http.getForObject(customers+"/api/customers",CustomerRow[].class)); }
    public List<SaleRow> sales() { return required(http.getForObject(orders+"/api/sales",SaleRow[].class)); }
    private <T> List<T> required(T[] rows) {
        if (rows==null) throw new org.springframework.web.client.RestClientException("Respuesta vacia");
        return Arrays.asList(rows);
    }
    public record UserRow(String cedula,String name,String email,String username,boolean enabled) {}
    public record CustomerRow(String cedula,String name,String email,String address,String phone) {}
    public record SaleRow(String customerCedula,String customerName,BigDecimal grandTotal) {}
}
