package com.nestigo.systemdesign.nestigo.dtos;

import com.nestigo.systemdesign.nestigo.entities.GuestEntity;
import com.nestigo.systemdesign.nestigo.entities.HotelEntity;
import com.nestigo.systemdesign.nestigo.entities.RoomEntity;
import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import com.nestigo.systemdesign.nestigo.entities.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class BookingDTO {

    private Long id;

    private HotelDTO hotel;

    private RoomDTO room;


    private UserDTO user;


    private Integer roomsCount;


    private LocalDate checkInDate;


    private LocalDate checkOutDate;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;



    private BookingStatus bookingStatus;


    private Set<GuestDTO> guests = new HashSet<>();


}
