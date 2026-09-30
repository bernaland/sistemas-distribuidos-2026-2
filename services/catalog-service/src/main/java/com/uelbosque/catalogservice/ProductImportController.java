package com.uelbosque.catalogservice;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
@RestController
public class ProductImportController {
    private final ProductImportService service;
    public ProductImportController(ProductImportService service) { this.service = service; }
    @PostMapping(value="/api/products/import", consumes="multipart/form-data")
    public Map<String, Object> upload(@RequestPart("file") MultipartFile file) {
        return Map.of("imported", service.replace(file), "message", "Archivo Cargado Exitosamente");
    }
}
