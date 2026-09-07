package com.uelbosque.orderservice;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
@Component
public class SaleDirectory {
    private final RestTemplate http;
    private final String customers;
    private final String users;
    private final String products;
    public SaleDirectory(RestTemplate http,@Value("${services.customers:http://localhost:8089}") String customers,
        @Value("${services.users:http://localhost:8081}") String users,@Value("${services.products:http://localhost:8082}") String products) {
        this.http=http; this.customers=customers; this.users=users; this.products=products;
    }
    public Customer customer(String cedula) { return get(customers+"/api/customers/{id}",Customer.class,cedula); }
    public Operator operator(String username) {
        Operator user=get(users+"/api/users/username/{id}",Operator.class,username);
        if (!user.enabled() || user.cedula()==null) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Operador inactivo o sin cedula");
        return user;
    }
    public Product product(String code) {
        Product product=get(products+"/api/products/code/{id}",Product.class,code);
        if (!product.active()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Producto inactivo");
        return product;
    }
    private <T> T get(String url,Class<T> type,String id) {
        try {
            T result=http.getForObject(url,type,id);
            if (result==null) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Respuesta vacia del servicio");
            return result;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Referencia inexistente: "+id);
        }
    }
    public record Customer(String cedula,String name) {}
    public record Operator(String cedula,String username,boolean enabled) {}
    public record Product(String code,String name,BigDecimal salePrice,BigDecimal ivaRate,boolean active) {}
}
