package com.uelbosque.catalogservice;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Component
public class SupplierDirectory {
    private final RestTemplate http;
    private final String url;
    public SupplierDirectory(RestTemplate http, @Value("${services.suppliers:http://localhost:8090}") String url) {
        this.http = http; this.url = url;
    }
    public void require(String nit) {
        if (nit == null || !nit.matches("[0-9]{1,20}")) throw new IllegalArgumentException("NIT obligatorio");
        try { http.getForObject(url + "/api/suppliers/{nit}", Object.class, nit); }
        catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proveedor no registrado: " + nit);
        }
    }
}
