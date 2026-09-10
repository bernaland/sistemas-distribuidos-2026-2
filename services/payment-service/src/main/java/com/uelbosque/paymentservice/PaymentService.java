package com.uelbosque.paymentservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service @Transactional
public class PaymentService {
    private final PaymentRepository repository;
    private final SaleLookup sales;
    public PaymentService(PaymentRepository repository,SaleLookup sales) { this.repository=repository; this.sales=sales; }
    public PaymentResponse pay(PaymentRequest request) {
        var prior=repository.findById(request.key());
        if (prior.isPresent()) return repeated(prior.get(),request);
        if (repository.existsByPaidSaleId(request.saleId())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Venta ya pagada");
        return repository.saveAndFlush(new Payment(request,sales.amount(request.saleId()))).response();
    }
    private PaymentResponse repeated(Payment payment,PaymentRequest request) {
        if (!payment.matches(request)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Clave usada por otro pago");
        return payment.response();
    }
    @Transactional(readOnly=true)
    public List<PaymentResponse> list() { return repository.findAll().stream().map(Payment::response).toList(); }
    @Transactional(readOnly=true)
    public PaymentResponse get(String key) {
        return repository.findById(key).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Pago inexistente")).response();
    }
}
