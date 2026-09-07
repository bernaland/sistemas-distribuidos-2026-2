package com.uelbosque.customerservice;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public CustomerData create(@Valid @RequestBody CustomerData data) { return service.create(data); }
    @GetMapping public List<CustomerData> list() { return service.list(); }
    @GetMapping("/{id}") public CustomerData get(@PathVariable String id) { return service.get(id); }
    @PutMapping("/{id}")
    public CustomerData update(@PathVariable String id, @Valid @RequestBody CustomerData data) { return service.update(id, data); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) { service.delete(id); }
}
