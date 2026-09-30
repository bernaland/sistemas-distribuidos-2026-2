package com.uelbosque.paymentservice;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;
@RestController @RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service=service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@Valid @RequestBody PaymentRequest request) { return service.pay(request); }
    @GetMapping public List<PaymentResponse> list() { return service.list(); }
    @GetMapping("/{key}") public PaymentResponse get(@PathVariable String key) { return service.get(key); }
}
