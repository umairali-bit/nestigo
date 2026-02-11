package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.dtos.HotelDTO;
import com.nestigo.systemdesign.nestigo.dtos.RoomDTO;
import com.nestigo.systemdesign.nestigo.entities.HotelEntity;
import com.nestigo.systemdesign.nestigo.entities.RoomEntity;
import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import com.nestigo.systemdesign.nestigo.exceptions.ResourceNotFoundException;
import com.nestigo.systemdesign.nestigo.exceptions.UnauthorizedException;
import com.nestigo.systemdesign.nestigo.repositories.HotelRepository;
import com.nestigo.systemdesign.nestigo.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.nestigo.systemdesign.nestigo.utils.AppUtils.getCurrentUser;


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

    // you can only create a room in a hotel if you are the owner
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("This user is not the owner of this hotel" + hotelId);
        }

        RoomEntity room = modelMapper.map(roomDTO, RoomEntity.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);


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
    //  you can only create a room in a hotel if you are the owner
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnauthorizedException("This user is not the owner of this room" + id);
        }
        inventoryService.deleteAllInventories(room);

        roomRepository.deleteById(id);
    }

    @Override
    public RoomDTO updateRoomById(Long hotelId, Long roomId, RoomDTO roomDTO) {
        log.info("Updating the room with ID: {}", hotelId);

//        getting hotel by id
        HotelEntity existingHotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID" + hotelId));
//        checking the owner
        UserEntity user = getCurrentUser();
        if(!user.equals(existingHotel.getOwner())){
            throw new UnauthorizedException("This user is not the owner of this hotel" + hotelId);
        }

        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room NOT found with ID: " + roomId));

        modelMapper.map(roomDTO, room);
        room.setId(roomId);

//        TODO: if price or inventory is updated, then update the inventory for this room

        room = roomRepository.save(room);

        return modelMapper.map(room, RoomDTO.class);

    }


}
