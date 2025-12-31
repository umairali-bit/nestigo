package com.nestigo.systemdesign.nestigo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HotelInfoDTO {

    private HotelDTO hotel;
    private List<RoomDTO> rooms;

}
