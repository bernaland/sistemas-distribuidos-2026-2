package com.uelbosque.inventoryservice;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface StockRepository extends JpaRepository<Stock,String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from Stock s where s.productCode=:code")
    Optional<Stock> locked(@Param("code") String code);
}
