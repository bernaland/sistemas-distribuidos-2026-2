package com.uelbosque.orderservice;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SaleRepository extends JpaRepository<Sale,Long> {
    Optional<Sale> findByIdempotencyKey(String idempotencyKey);
}
