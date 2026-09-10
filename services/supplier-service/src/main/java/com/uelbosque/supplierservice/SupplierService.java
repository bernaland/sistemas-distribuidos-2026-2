package com.uelbosque.supplierservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service
@Transactional
public class SupplierService {
    private final SupplierRepository repository;
    public SupplierService(SupplierRepository repository) { this.repository = repository; }
    public SupplierData create(SupplierData data) {
        if (repository.existsById(data.nit())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Identificador ya registrado");
        return repository.saveAndFlush(new Supplier(data)).data();
    }
    @Transactional(readOnly=true)
    public List<SupplierData> list() { return repository.findAll().stream().map(Supplier::data).toList(); }
    @Transactional(readOnly=true)
    public SupplierData get(String id) { return find(id).data(); }
    public SupplierData update(String id, SupplierData data) {
        if (!id.equals(data.nit())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede cambiar el identificador");
        Supplier record = find(id);
        record.update(data);
        return repository.saveAndFlush(record).data();
    }
    public void delete(String id) { repository.delete(find(id)); }
    private Supplier find(String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro inexistente"));
    }
}
