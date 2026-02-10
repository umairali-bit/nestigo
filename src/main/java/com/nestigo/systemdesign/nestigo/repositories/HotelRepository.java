package com.nestigo.systemdesign.nestigo.repositories;

import com.nestigo.systemdesign.nestigo.entities.HotelEntity;
import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long> {
    List<HotelEntity> findByOwner(UserEntity user);
}
