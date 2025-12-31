package com.nestigo.systemdesign.nestigo.repositories;

import com.nestigo.systemdesign.nestigo.dtos.HotelPriceDTO;
import com.nestigo.systemdesign.nestigo.entities.HotelEntity;
import com.nestigo.systemdesign.nestigo.entities.HotelMinPriceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPriceEntity, Integer> {

    @Query("""
            SELECT new com.nestigo.systemdesign.nestigo.dtos.HotelPriceDTO(i.hotel, AVG(i.price))
            FROM HotelMinPriceEntity i
            WHERE i.hotel.city = :city
              AND i.date BETWEEN :startDate AND :endDate
              AND i.hotel.active = true
            GROUP BY i.hotel
            HAVING COUNT(i.date) = :dateCount
            """)
    Page<HotelPriceDTO> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );


    Optional<HotelMinPriceEntity> findByHotelAndDate(HotelEntity hotelEntity, LocalDate date);
}
