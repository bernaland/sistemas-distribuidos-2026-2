package com.uelbosque.orderservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service @Transactional
public class SaleService {
    private final SaleRepository repository;
    private final SaleDirectory directory;
    public SaleService(SaleRepository repository,SaleDirectory directory) { this.repository=repository; this.directory=directory; }
    public SaleResponse create(String key,SaleRequest request,String username) {
        if (key==null || key.isBlank() || key.length()>100) throw new IllegalArgumentException("Idempotency-Key requerido, maximo 100 caracteres");
        String fingerprint=username+":"+request.toString();
        var previous=repository.findByIdempotencyKey(key);
        if (previous.isPresent()) return repeated(previous.get(),fingerprint);
        var customer=directory.customer(request.customerCedula());
        var operator=directory.operator(username);
        var lines=request.items().stream().map(i -> new SaleLine(directory.product(i.productCode()),i.quantity())).toList();
        return repository.saveAndFlush(new Sale(key,fingerprint,customer,operator,lines)).response();
    }
    private SaleResponse repeated(Sale sale,String fingerprint) {
        if (!sale.matches(fingerprint)) throw new ResponseStatusException(HttpStatus.CONFLICT,"La clave pertenece a otra venta");
        return sale.response();
    }
    @Transactional(readOnly=true)
    public List<SaleResponse> list() { return repository.findAll().stream().map(Sale::response).toList(); }
    @Transactional(readOnly=true)
    public SaleResponse get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Venta inexistente")).response();
    }
}
