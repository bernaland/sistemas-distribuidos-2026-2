package com.uelbosque.orderservice;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
@RestController @RequestMapping("/api/sales")
public class SaleController {
    private final SaleService service;
    public SaleController(SaleService service) { this.service=service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SaleResponse create(@RequestHeader("Idempotency-Key") String key,@Valid @RequestBody SaleRequest request,Principal principal) {
        return service.create(key,request,principal.getName());
    }
    @GetMapping public List<SaleResponse> list() { return service.list(); }
    @GetMapping("/{id}") public SaleResponse get(@PathVariable Long id) { return service.get(id); }
}
