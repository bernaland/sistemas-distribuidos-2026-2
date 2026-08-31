package com.uelbosque.catalogservice.service;

import com.uelbosque.catalogservice.dto.ProductRequestDto;
import com.uelbosque.catalogservice.dto.ProductResponseDto;
import com.uelbosque.catalogservice.exception.DuplicateResourceException;
import com.uelbosque.catalogservice.exception.ResourceNotFoundException;
import com.uelbosque.catalogservice.model.Product;
import com.uelbosque.catalogservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product sampleProduct;
    private ProductRequestDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("P001", "Arroz", "Arroz 1kg",
                new BigDecimal("3000.00"), new BigDecimal("4000.00"),
                new BigDecimal("19.00"), "Granos", "http://img.jpg");
        sampleProduct.setId(1L);

        sampleDto = new ProductRequestDto();
        sampleDto.setCode("P001");
        sampleDto.setName("Arroz");
        sampleDto.setDescription("Arroz 1kg");
        sampleDto.setPurchasePrice(new BigDecimal("3000.00"));
        sampleDto.setSalePrice(new BigDecimal("4000.00"));
        sampleDto.setIvaRate(new BigDecimal("19.00"));
        sampleDto.setCategory("Granos");
        sampleDto.setImageUrl("http://img.jpg");
    }

    @Test
    @DisplayName("Debe crear un producto correctamente cuando el código no existe")
    void shouldCreateProductSuccessfully() {
        when(repository.existsByCode("P001")).thenReturn(false);
        when(repository.save(any(Product.class))).thenReturn(sampleProduct);

        ProductResponseDto response = service.createProduct(sampleDto);

        assertNotNull(response);
        assertEquals("P001", response.getCode());
        assertEquals("Arroz", response.getName());
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe lanzar DuplicateResourceException cuando el código ya existe")
    void shouldThrowWhenCreatingDuplicateProduct() {
        when(repository.existsByCode("P001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.createProduct(sampleDto));
        verify(repository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe obtener un producto por ID cuando existe y está activo")
    void shouldGetProductById() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        ProductResponseDto response = service.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Arroz", response.getName());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
    void shouldThrowWhenProductNotFoundById() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getProductById(99L));
    }

    @Test
    @DisplayName("Debe listar todos los productos activos")
    void shouldListAllActiveProducts() {
        when(repository.findByActiveTrue()).thenReturn(List.of(sampleProduct));

        List<ProductResponseDto> list = service.getAllProducts();

        assertEquals(1, list.size());
        assertEquals("P001", list.get(0).getCode());
    }

    @Test
    @DisplayName("Debe realizar soft delete desactivando el producto")
    void shouldSoftDeleteProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(repository.save(any(Product.class))).thenReturn(sampleProduct);

        service.deleteProduct(1L);

        assertFalse(sampleProduct.isActive());
        verify(repository, times(1)).save(sampleProduct);
    }
}
