package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.dtos.HotelDTO;
import com.nestigo.systemdesign.nestigo.dtos.RoomDTO;
import com.nestigo.systemdesign.nestigo.entities.HotelEntity;
import com.nestigo.systemdesign.nestigo.entities.RoomEntity;
import com.nestigo.systemdesign.nestigo.exceptions.ResourceNotFoundException;
import com.nestigo.systemdesign.nestigo.repositories.HotelRepository;
import com.nestigo.systemdesign.nestigo.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;


    @Override
    public RoomDTO createRoom(Long hotelId, RoomDTO roomDTO) {
        log.info("Creating a room in hotel with ID: {}", hotelId);


    //  checking if the hotel exists or not
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel NOT found with ID: " + hotelId));

        RoomEntity room = modelMapper.map(roomDTO, RoomEntity.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);



    //    TODO: add inventory - done

        if(hotel.isActive()) {
            inventoryService.initializeRoomForAYear(room);

        }
        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    public List<RoomDTO> getAllRoomsByHotelId(Long hotelId) {
        log.info("Getting all rooms in hotel with ID: {}", hotelId);

    //  checking if the hotel exists or not
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel NOT found with ID: " + hotelId));
        return hotel.getRooms()
                .stream()
                .map(room -> modelMapper.map(room, RoomDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDTO getRoomById(Long id) {
        log.info("Getting the room in hotel with ID: {}", id);

        //  checking if the room exists or not
        RoomEntity room = roomRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel NOT found with ID: " + id));
        return modelMapper.map(room, RoomDTO.class);
    }

    @Transactional
    @Override
    public void deleteRoomById(Long id) {
        log.info("Deleting room with ID: {}", id);
        RoomEntity room = roomRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel NOT found with ID: " + id));

        inventoryService.deleteAllInventories(room);

        roomRepository.deleteById(id);




    }
}
