package com.uelbosque.catalogservice;

import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface CatalogLockRepository extends JpaRepository<CatalogWriteLock, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from CatalogWriteLock l where l.id=1")
    Optional<CatalogWriteLock> acquire();
}
