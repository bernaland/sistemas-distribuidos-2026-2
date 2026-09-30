package com.uelbosque.cartservice;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
@Component
public class CatalogReader {
    private final RestTemplate http;
    private final String url;
    public CatalogReader(RestTemplate http,@Value("${services.products:http://localhost:8082}") String url) { this.http=http; this.url=url; }
    public Product get(String code) {
        try {
            Product product=http.getForObject(url+"/api/products/code/{code}",Product.class,code);
            if (product==null || !product.active()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Producto no disponible");
            return product;
        } catch (HttpClientErrorException.NotFound e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Producto inexistente"); }
    }
    public record Product(String code,String name,BigDecimal salePrice,BigDecimal ivaRate,boolean active) {}
}
