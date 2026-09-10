package com.uelbosque.catalogservice.service;

import com.uelbosque.catalogservice.dto.ProductRequestDto;
import com.uelbosque.catalogservice.dto.ProductResponseDto;
import com.uelbosque.catalogservice.exception.DuplicateResourceException;
import com.uelbosque.catalogservice.exception.ResourceNotFoundException;
import com.uelbosque.catalogservice.model.Product;
import com.uelbosque.catalogservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repository;
    private final com.uelbosque.catalogservice.SupplierDirectory suppliers;
    private final com.uelbosque.catalogservice.CatalogMutex mutex;

    public ProductService(ProductRepository repository, com.uelbosque.catalogservice.SupplierDirectory suppliers, com.uelbosque.catalogservice.CatalogMutex mutex) {
        this.repository = repository;
        this.suppliers = suppliers;
        this.mutex = mutex;
    }

    public ProductResponseDto createProduct(ProductRequestDto dto) {
        suppliers.require(dto.getSupplierNit());
        mutex.acquire();
        if (repository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("El producto con código " + dto.getCode() + " ya existe");
        }
        Product product = new Product(dto.getCode(), dto.getName(), dto.getDescription(),
                dto.getPurchasePrice(), dto.getSalePrice(), dto.getIvaRate(),
                dto.getCategory(), dto.getImageUrl());
        product.setSupplierNit(dto.getSupplierNit());
        return mapToDto(repository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return repository.findByActiveTrue().stream().map(this::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        return repository.findById(id)
                .filter(Product::isActive)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductByCode(String code) {
        return repository.findByCodeAndActiveTrue(code)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código: " + code));
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        suppliers.require(dto.getSupplierNit());
        mutex.acquire();
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        if (!product.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("El código " + dto.getCode() + " ya pertenece a otro producto");
        }
        applyDtoToProduct(product, dto);
        product.setSupplierNit(dto.getSupplierNit());
        return mapToDto(repository.save(product));
    }

    public void deleteProduct(Long id) {
        mutex.acquire();
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        product.setActive(false);
        repository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProducts(String query, String category) {
        if (category != null && !category.isBlank()) {
            return repository.findByCategoryIgnoreCaseAndActiveTrue(category).stream()
                    .map(this::mapToDto).toList();
        }
        if (query != null && !query.isBlank()) {
            return repository.findByNameContainingIgnoreCaseAndActiveTrue(query).stream()
                    .map(this::mapToDto).toList();
        }
        return getAllProducts();
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return repository.findDistinctCategories();
    }

    private void applyDtoToProduct(Product product, ProductRequestDto dto) {
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPurchasePrice(dto.getPurchasePrice());
        product.setSalePrice(dto.getSalePrice());
        product.setIvaRate(dto.getIvaRate());
        product.setCategory(dto.getCategory());
        product.setImageUrl(dto.getImageUrl());
    }

    private ProductResponseDto mapToDto(Product product) {
        ProductResponseDto result = new ProductResponseDto(
                product.getId(), product.getCode(), product.getName(),
                product.getDescription(), product.getPurchasePrice(), product.getSalePrice(),
                product.getIvaRate(), product.getCategory(), product.getImageUrl(),
                product.isActive()
        );
        result.setSupplierNit(product.getSupplierNit());
        return result;
    }
}
