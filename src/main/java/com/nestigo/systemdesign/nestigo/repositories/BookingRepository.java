package com.nestigo.systemdesign.nestigo.repositories;

import com.nestigo.systemdesign.nestigo.entities.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Optional<BookingEntity> findByPaymentSessionId(String sessionId);
}
