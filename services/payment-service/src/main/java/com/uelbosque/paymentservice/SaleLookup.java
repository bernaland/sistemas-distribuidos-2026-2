package com.uelbosque.paymentservice;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
@Component
public class SaleLookup {
    private final RestTemplate http;
    private final String url;
    public SaleLookup(RestTemplate http,@Value("${services.orders:http://localhost:8085}") String url) { this.http=http; this.url=url; }
    public BigDecimal amount(Long saleId) {
        try {
            SaleTotal total=http.getForObject(url+"/api/sales/{id}",SaleTotal.class,saleId);
            if (total==null || total.grandTotal()==null) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Venta sin total");
            return total.grandTotal();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Venta inexistente");
        }
    }
    public record SaleTotal(BigDecimal grandTotal) {}
}
