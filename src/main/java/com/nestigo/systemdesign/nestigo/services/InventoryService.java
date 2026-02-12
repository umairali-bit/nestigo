package com.nestigo.systemdesign.nestigo.services;


import com.nestigo.systemdesign.nestigo.dtos.*;
import com.nestigo.systemdesign.nestigo.entities.RoomEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(RoomEntity room);

    void deleteAllInventories (RoomEntity room);

    Page<HotelPriceDTO>  searchHotels(HotelSearchRequest hotelSearchRequest);


    List<InventoryDTO> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, InventoryUpdateRequestDTO inventoryUpdateRequestDTO);
}
