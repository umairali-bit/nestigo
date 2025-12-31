package com.nestigo.systemdesign.nestigo.dtos;

import com.nestigo.systemdesign.nestigo.entities.HotelEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RoomDTO {

    private Long id;
    private String type;
    private BigDecimal basePrice;
    private Integer totalCount;
    private Integer capacity;
    private String[] photos;
    private String[] amenities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAT;
}
