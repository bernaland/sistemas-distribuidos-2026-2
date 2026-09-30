package com.uelbosque.notificationservice;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface NotificationRepository extends JpaRepository<Notification,String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select n from Notification n where n.notificationKey=:key")
    Optional<Notification> locked(@Param("key") String key);
}
