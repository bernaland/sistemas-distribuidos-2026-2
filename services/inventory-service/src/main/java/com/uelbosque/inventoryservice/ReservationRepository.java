package com.uelbosque.inventoryservice;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface ReservationRepository extends JpaRepository<Reservation,String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select r from Reservation r where r.reservationKey=:key")
    Optional<Reservation> locked(@Param("key") String key);
}
