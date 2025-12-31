package com.nestigo.systemdesign.nestigo.dtos;


import com.nestigo.systemdesign.nestigo.entities.HotelEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceDTO {

    private HotelEntity hotel;
    private double price;

}
