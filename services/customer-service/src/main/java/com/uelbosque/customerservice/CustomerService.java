package com.uelbosque.customerservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service
@Transactional
public class CustomerService {
    private final CustomerRepository repository;
    public CustomerService(CustomerRepository repository) { this.repository = repository; }
    public CustomerData create(CustomerData data) {
        if (repository.existsById(data.cedula())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Identificador ya registrado");
        return repository.saveAndFlush(new Customer(data)).data();
    }
    @Transactional(readOnly=true)
    public List<CustomerData> list() { return repository.findAll().stream().map(Customer::data).toList(); }
    @Transactional(readOnly=true)
    public CustomerData get(String id) { return find(id).data(); }
    public CustomerData update(String id, CustomerData data) {
        if (!id.equals(data.cedula())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede cambiar el identificador");
        Customer record = find(id);
        record.update(data);
        return repository.saveAndFlush(record).data();
    }
    public void delete(String id) { repository.delete(find(id)); }
    private Customer find(String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro inexistente"));
    }
}
