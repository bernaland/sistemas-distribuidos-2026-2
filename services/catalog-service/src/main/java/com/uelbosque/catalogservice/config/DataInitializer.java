package com.uelbosque.catalogservice.config;

import com.uelbosque.catalogservice.dto.ProductRequestDto;
import com.uelbosque.catalogservice.repository.ProductRepository;
import com.uelbosque.catalogservice.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository repository;
    private final ProductService service;

    public DataInitializer(ProductRepository repository, ProductService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            seedSampleProducts();
        }
    }

    private void seedSampleProducts() {
        createSample("PROD-001", "Arroz Diana 1kg", "Arroz blanco premium 1000g",
                new BigDecimal("3500.00"), new BigDecimal("4500.00"), new BigDecimal("19.00"), "Abarrotes");
        createSample("PROD-002", "Aceite Premier 1000ml", "Aceite vegetal 1000ml",
                new BigDecimal("8000.00"), new BigDecimal("10500.00"), new BigDecimal("19.00"), "Abarrotes");
        createSample("PROD-003", "Leche Entera Alquería 1L", "Leche pasteurizada 1000ml",
                new BigDecimal("3800.00"), new BigDecimal("4800.00"), new BigDecimal("0.00"), "Lácteos");
        createSample("PROD-004", "Café Sello Rojo 500g", "Café molido tradicional",
                new BigDecimal("12000.00"), new BigDecimal("15500.00"), new BigDecimal("19.00"), "Bebidas");
    }

    private void createSample(String code, String name, String desc,
                              BigDecimal buy, BigDecimal sell, BigDecimal iva, String cat) {
        ProductRequestDto dto = new ProductRequestDto();
        dto.setCode(code);
        dto.setName(name);
        dto.setDescription(desc);
        dto.setPurchasePrice(buy);
        dto.setSalePrice(sell);
        dto.setIvaRate(iva);
        dto.setCategory(cat);
        dto.setImageUrl("https://via.placeholder.com/150");
        service.createProduct(dto);
    }
}
