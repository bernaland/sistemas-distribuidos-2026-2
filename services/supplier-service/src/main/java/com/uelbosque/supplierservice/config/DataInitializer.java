package com.uelbosque.supplierservice.config;

import com.uelbosque.supplierservice.SupplierData;
import com.uelbosque.supplierservice.SupplierRepository;
import com.uelbosque.supplierservice.SupplierService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataInitializer implements CommandLineRunner {

    private final SupplierRepository repository;
    private final SupplierService service;

    public DataInitializer(SupplierRepository repository, SupplierService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            service.create(new SupplierData("900112233", "Proveedor Principal S.A.S.", "Calle 100 # 15-20", "3001234567", "Bogota"));
        }
    }
}
