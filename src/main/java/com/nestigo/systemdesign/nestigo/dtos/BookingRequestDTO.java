package com.nestigo.systemdesign.nestigo.dtos;


import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    private Long hotelId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer roomsCount;







}
