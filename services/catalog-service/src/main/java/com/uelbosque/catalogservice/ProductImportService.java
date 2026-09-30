package com.uelbosque.catalogservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.uelbosque.catalogservice.repository.ProductRepository;
@Service
public class ProductImportService {
    private final ProductCsvParser parser;
    private final SupplierDirectory suppliers;
    private final ProductRepository repository;
    private final CatalogMutex mutex;
    public ProductImportService(ProductCsvParser parser, SupplierDirectory suppliers, ProductRepository repository, CatalogMutex mutex) {
        this.parser = parser; this.suppliers = suppliers; this.repository = repository; this.mutex = mutex;
    }
    @Transactional
    public int replace(MultipartFile file) {
        var products = parser.parse(file);
        products.stream().map(p -> p.getSupplierNit()).distinct().forEach(suppliers::require);
        mutex.acquire();
        repository.deleteAllInBatch();
        repository.saveAllAndFlush(products);
        return products.size();
    }
}
