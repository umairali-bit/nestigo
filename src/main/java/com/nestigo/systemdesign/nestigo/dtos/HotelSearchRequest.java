package com.nestigo.systemdesign.nestigo.dtos;


import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequest {

    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer roomCount;


    //pagination by default
    private Integer page = 0;
    private Integer pageSize = 10;



}
