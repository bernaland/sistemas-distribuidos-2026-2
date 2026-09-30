package com.uelbosque.catalogservice.repository;

import com.uelbosque.catalogservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);
    Optional<Product> findByCodeAndActiveTrue(String code);
    List<Product> findByActiveTrue();
    List<Product> findByCategoryIgnoreCaseAndActiveTrue(String category);
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    boolean existsByCode(String code);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true AND p.category IS NOT NULL AND p.category <> ''")
    List<String> findDistinctCategories();
}
