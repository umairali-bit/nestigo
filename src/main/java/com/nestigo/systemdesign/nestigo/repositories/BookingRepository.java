package com.nestigo.systemdesign.nestigo.repositories;

import com.nestigo.systemdesign.nestigo.entities.BookingEntity;
import com.nestigo.systemdesign.nestigo.entities.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Optional<BookingEntity> findByPaymentSessionId(String sessionId);

    List<BookingEntity> findByHotel(HotelEntity hotel);
}
