package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.dtos.HotelDTO;
import com.nestigo.systemdesign.nestigo.dtos.HotelInfoDTO;
import com.nestigo.systemdesign.nestigo.entities.HotelEntity;

public interface HotelService {

    HotelDTO createHotel(HotelDTO hotelDTO);

    HotelDTO getHotelById(Long id);

    HotelDTO updateHotel(Long id, HotelDTO hotelDTO);

    void deleteHotelById(Long id);

    void activateHotel(Long id);

    HotelInfoDTO getHotelInfoById(Long hotelId);
}
