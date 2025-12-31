package com.nestigo.systemdesign.nestigo.services;


import com.nestigo.systemdesign.nestigo.dtos.HotelDTO;
import com.nestigo.systemdesign.nestigo.dtos.HotelPriceDTO;
import com.nestigo.systemdesign.nestigo.dtos.HotelSearchRequest;
import com.nestigo.systemdesign.nestigo.entities.RoomEntity;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(RoomEntity room);

    void deleteAllInventories (RoomEntity room);

    Page<HotelPriceDTO>  searchHotels(HotelSearchRequest hotelSearchRequest);



}
