package com.uelbosque.supplierservice;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService service;
    public SupplierController(SupplierService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SupplierData create(@Valid @RequestBody SupplierData data) { return service.create(data); }
    @GetMapping public List<SupplierData> list() { return service.list(); }
    @GetMapping("/{id}") public SupplierData get(@PathVariable String id) { return service.get(id); }
    @PutMapping("/{id}")
    public SupplierData update(@PathVariable String id, @Valid @RequestBody SupplierData data) { return service.update(id, data); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) { service.delete(id); }
}
