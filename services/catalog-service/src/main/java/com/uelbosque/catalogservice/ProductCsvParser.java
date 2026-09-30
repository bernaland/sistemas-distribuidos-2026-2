package com.uelbosque.catalogservice;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.*;
import com.uelbosque.catalogservice.model.Product;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.util.*;
@Component
public class ProductCsvParser {
    public List<Product> parse(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Seleccione un archivo");
        if (!Objects.toString(file.getOriginalFilename(), "").toLowerCase(Locale.ROOT).endsWith(".csv"))
            throw new IllegalArgumentException("Formato de archivo invalido");
        try (var reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             var parser = CSVFormat.DEFAULT.builder().setTrim(true).build().parse(reader)) {
            return rows(parser);
        } catch (IOException | UncheckedIOException | IllegalStateException e) { throw new IllegalArgumentException("CSV invalido", e); }
    }
    private List<Product> rows(CSVParser parser) {
        List<Product> products = new ArrayList<>();
        Set<String> codes = new HashSet<>();
        for (CSVRecord row : parser) {
            if (row.size() != 6) throw new IllegalArgumentException("Se requieren seis columnas");
            if (row.getRecordNumber() == 1 && row.get(0).replace("\uFEFF", "").equalsIgnoreCase("codigo_producto")) continue;
            Product product = product(row);
            if (!codes.add(product.getCode())) throw new IllegalArgumentException("Codigo duplicado");
            products.add(product);
        }
        if (products.isEmpty()) throw new IllegalArgumentException("Archivo sin productos");
        return products;
    }
    private Product product(CSVRecord row) {
        if (row.size() != 6) throw new IllegalArgumentException("Se requieren seis columnas");
        String code = identifier(row.get(0).replace("\uFEFF", ""));
        String nit = identifier(row.get(2));
        if (row.get(1).isBlank() || row.get(1).length() > 50) throw new IllegalArgumentException("Nombre invalido");
        BigDecimal iva = amount(row.get(4));
        if (iva.compareTo(new BigDecimal("100")) > 0) throw new IllegalArgumentException("IVA invalido");
        Product product = new Product(code, row.get(1), null, amount(row.get(3)), amount(row.get(5)), iva, null, null);
        product.setSupplierNit(nit);
        return product;
    }
    private String identifier(String text) {
        if (!text.matches("[0-9]{1,20}")) throw new IllegalArgumentException("Identificador numerico invalido");
        return text;
    }
    private BigDecimal amount(String text) {
        BigDecimal value = new BigDecimal(text);
        if (value.signum() < 0 || value.scale() > 2 || value.precision() - value.scale() > 10)
            throw new IllegalArgumentException("Valor monetario invalido");
        return value;
    }
}
