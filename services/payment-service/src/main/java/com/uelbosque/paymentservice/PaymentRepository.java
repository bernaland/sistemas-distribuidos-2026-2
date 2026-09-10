package com.uelbosque.paymentservice;

import org.springframework.data.jpa.repository.JpaRepository;
public interface PaymentRepository extends JpaRepository<Payment,String> {
    boolean existsByPaidSaleId(Long saleId);
}
